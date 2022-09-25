package com.kuldeep.atm1

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kuldeep.atm1.adapter.TransactionAdapter
import com.kuldeep.atm1.data.Transactions
import com.kuldeep.atm1.databinding.ActivityMainBinding
import com.kuldeep.atm1.db.TransactionDB
import com.kuldeep.atm1.repository.TransRepo
import com.kuldeep.atm1.utils.datastore.DataStoreManager
import com.kuldeep.atm1.viewmodel.AtmVMProvider
import com.kuldeep.atm1.viewmodel.AtmViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var vm: AtmViewModel
    lateinit var dataStoreManager: DataStoreManager
    lateinit var transcAdapter: TransactionAdapter

    //Atm's total balance
    private var balanceInAtm = 100000

    //Total 2000's notes
    private var notes_2000 = 25

    //Total 500's notes
    private var notes_500 = 70

    //Total 200's notes
    private var notes_200 = 65

    //Total 100's notes
    private var notes_100 = 20

    lateinit var dataModel: Transactions
    lateinit var lasttrans: MutableLiveData<List<Transactions>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        dataStoreManager = DataStoreManager(this)
        val repository = TransRepo(TransactionDB(this))
        val vmProviderFactory = AtmVMProvider(repository)
        lasttrans = MutableLiveData()

        vm = ViewModelProvider(this, vmProviderFactory).get(AtmViewModel::class.java)

        getNoOfNotes()
        makeATM()
        setUpRecyclerView()

        vm.allTransactions.observe(this, Observer { list ->
            list?.let {
                transcAdapter.differ.submitList(it)
                if (list.isNotEmpty()) {
                    lasttrans.postValue(listOf(list.last()))
                }

            }
        })

        lasttrans.observe(this, Observer {
            binding.tvLastAmt.text = "Rs. " + it[0].totalAmt.toString()
            binding.tvLastTwoTh.text = it[0].twoThsd.toString()
            binding.tvLastFiveHn.text = it[0].fiveHndrd.toString()
            binding.tvLastTwoHn.text = it[0].twoHndrd.toString()
            binding.tvLastOneHn.text = it[0].oneHndrd.toString()
        })
    }

    private fun makeATM() {
        binding.btnWithdraw.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.edtEnterAmount.text.toString()) || binding.edtEnterAmount.text.toString()
                    .toInt() % 100 != 0 || binding.edtEnterAmount.text.toString() == "0" -> Toast.makeText(
                    this,
                    R.string.str_amount_validation,
                    Toast.LENGTH_SHORT
                ).show()
                balanceInAtm >= binding.edtEnterAmount.text.toString()
                    .toInt() -> {
                    getNotes(binding.edtEnterAmount.text.toString().toInt())
                    binding.edtEnterAmount.setText("")
                }
                else -> {
                    Toast.makeText(this, R.string.str_insuf_amount, Toast.LENGTH_SHORT).show()
                    binding.edtEnterAmount.setText("")
                }
            }
        }
    }

    //for 2000's, 500's, 200's, 100's notes
    val notes = intArrayOf(2000, 500, 200, 100)

    //store per note's count
    var noteCounter = IntArray(4)

    /*
       count of notes
       store value to the localDatabase
    */
    private fun getNotes(amountTobePassed: Int) {
        noteCounter = IntArray(4)
        var amount = amountTobePassed
        for (i in 0..3) {
            when {
                notes[i] == 2000 && notes_2000 != 0 -> {
                    val totalNotes = amount / notes[i]
                    if (notes_2000 < totalNotes) {
                        noteCounter[i] = notes_2000
                        amount -= (notes_2000 * 2000)
                    } else {
                        noteCounter[i] = totalNotes
                        amount %= notes[i]
                    }

                }
                notes[i] == 500 && notes_500 != 0 -> {
                    val totalNotes = amount / notes[i]
                    if (notes_500 < totalNotes) {
                        noteCounter[i] = notes_500
                        amount -= (notes_500 * 500)
                    } else {
                        noteCounter[i] = totalNotes
                        amount %= notes[i]
                    }
                }
                notes[i] == 200 && notes_200 != 0 -> {
                    val totalNotes = amount / notes[i]
                    if (notes_200 < totalNotes) {
                        noteCounter[i] = notes_200
                        amount -= (notes_200 * 200)
                    } else {
                        noteCounter[i] = totalNotes
                        amount %= notes[i]
                    }
                }
                notes[i] == 100 && notes_100 != 0 -> {
                    val totalNotes = amount / notes[i]
                    if (notes_100 < totalNotes) {
                        noteCounter[i] = notes_100
                        amount -= (notes_100 * 100)
                    } else {
                        noteCounter[i] = totalNotes
                        amount %= notes[i]
                    }

                }
            }
        }

        val final_2000notes = noteCounter[0]
        val final_500notes = noteCounter[1]
        val final_200notes = noteCounter[2]
        val final_100notes = noteCounter[3]

        // Print notes
        if (notes_2000 - final_2000notes >= 0)
            notes_2000 -= final_2000notes
        if (notes_500 - final_500notes >= 0)
            notes_500 -= final_500notes
        if (notes_200 - final_200notes >= 0)
            notes_200 -= final_200notes
        if (notes_100 - final_100notes >= 0)
            notes_100 -= final_100notes

        dataModel = Transactions(
            id = System.currentTimeMillis(),
            totalAmt = amountTobePassed,
            twoThsd = final_2000notes,
            fiveHndrd = final_500notes,
            twoHndrd = final_200notes,
            oneHndrd = final_100notes
        )

        vm.withdrawAmt(dataModel)

        balanceInAtm -= amountTobePassed

        CoroutineScope(Dispatchers.IO).launch {
            dataStoreManager.saveDataString(
                TOTAL_AMT,
                balanceInAtm
            )

            dataStoreManager.saveDataString(
                NOTE_2000,
                notes_2000
            )

            dataStoreManager.saveDataString(
                NOTE_500,
                notes_500
            )

            dataStoreManager.saveDataString(
                NOTE_200,
                notes_200
            )
            dataStoreManager.saveDataString(
                NOTE_100,
                notes_100
            )
        }
        setNotesValues()
    }

    private fun setUpRecyclerView() {
        transcAdapter = TransactionAdapter()
        binding.recView.apply {
            adapter = transcAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    /*
        save data to dataStore
    */
    fun getNoOfNotes() {

        CoroutineScope(Dispatchers.IO).launch {

            CoroutineScope(Dispatchers.Main).launch {
                if (dataStoreManager.getDataStore(TOTAL_AMT) != "null") {
                    balanceInAtm = dataStoreManager.getDataStore(
                        TOTAL_AMT
                    ).toInt()

                    notes_2000 = dataStoreManager.getDataStore(
                        NOTE_2000
                    ).toInt()
                    notes_500 = dataStoreManager.getDataStore(
                        NOTE_500
                    ).toInt()
                    notes_200 = dataStoreManager.getDataStore(
                        NOTE_200
                    ).toInt()
                    notes_100 = dataStoreManager.getDataStore(
                        NOTE_100
                    ).toInt()

                    setNotesValues()
                } else
                    setNotesValues()

            }
        }

        dataModel = Transactions(
            id = System.currentTimeMillis(),
            totalAmt = balanceInAtm,
            twoThsd = binding.tvMainTwoTh.text.toString().toInt(),
            fiveHndrd = binding.tvMainFiveHn.text.toString().toInt(),
            twoHndrd = binding.tvMainTwoHn.text.toString().toInt(),
            oneHndrd = binding.tvMainOneHn.text.toString().toInt()
        )

    }

    fun setNotesValues() {
        CoroutineScope(Dispatchers.Main).launch {
            binding.tvMainTotalAmt.text = "Rs. " + balanceInAtm
            binding.tvMainTwoTh.text = notes_2000.toString()
            binding.tvMainFiveHn.text = notes_500.toString()
            binding.tvMainTwoHn.text = notes_200.toString()
            binding.tvMainOneHn.text = notes_100.toString()
        }
    }

    companion object {
        val TOTAL_AMT = "TOTAL_AMOUNT"
        val NOTE_2000 = "NOTE_2000"
        val NOTE_500 = "NOTE_500"
        val NOTE_200 = "NOTE_200"
        val NOTE_100 = "NOTE_100"
    }

}