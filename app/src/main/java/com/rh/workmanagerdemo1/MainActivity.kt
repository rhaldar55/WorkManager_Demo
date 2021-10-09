package com.rh.workmanagerdemo1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.work.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            //setOneTimeWorkRequest()
            //setOneTimeWorkRequest1()
            // setOneTimeWorkRequest2()
            //setOneTimeWorkRequest3()
            //setOneTimeWorkRequest4()


            setPeriodicWorkrequest()
        }
    }

    //OneTime Work
    private fun setOneTimeWorkRequest() {
        val workManager: WorkManager = WorkManager.getInstance(applicationContext)

        val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .build()

        //WorkManager.getInstance(applicationContext).enqueue(uploadRequest)
        workManager.enqueue(uploadRequest)

        //Get Status Update
        workManager.getWorkInfoByIdLiveData(uploadRequest.id).observe(this, Observer {
            textView.text = it.state.name.toString()
        })
    }


    //OneTime Work with Constraints - Charging / INTERNET
    private fun setOneTimeWorkRequest1() {
        val workManager: WorkManager = WorkManager.getInstance(applicationContext)

        //Constraints - Charging
        //val constraints = Constraints.Builder().setRequiresCharging(true).build()

        //Constraints - NetworkType.CONNECTED
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(uploadRequest)

        //Get Status Update
        workManager.getWorkInfoByIdLiveData(uploadRequest.id).observe(this, Observer {
            textView.text = it.state.name.toString()
        })
    }


    //Set Input and Output
    private fun setOneTimeWorkRequest2() {
        val workManager: WorkManager = WorkManager.getInstance(applicationContext)
        val data: Data = Data.Builder()
            .putInt(KEY_COUNT_VALUE, 125)
            .build()

        //Constraints - NetworkType.CONNECTED
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        workManager.enqueue(uploadRequest)

        //Get Status Update
        workManager.getWorkInfoByIdLiveData(uploadRequest.id).observe(this, Observer {
            textView.text = it.state.name.toString()

            if (it.state.isFinished) {
                val data = it.outputData
                val message = data.getString(UploadWorker.KEY_WORKER)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //Chaining
    private fun setOneTimeWorkRequest3() {
        val workManager: WorkManager = WorkManager.getInstance(applicationContext)
        val data: Data = Data.Builder()
            .putInt(KEY_COUNT_VALUE, 125)
            .build()

        //Constraints - NetworkType.CONNECTED
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        val filteringRequest = OneTimeWorkRequest.Builder(FilteringWorker::class.java)
            .build()
        val compressingRequest = OneTimeWorkRequest.Builder(CompressWorker::class.java)
            .build()

        workManager
            .beginWith(filteringRequest)
            .then(compressingRequest)
            .then(uploadRequest)
            .enqueue()

        //Get Status Update
        workManager.getWorkInfoByIdLiveData(uploadRequest.id).observe(this, Observer {
            textView.text = it.state.name.toString()

            if (it.state.isFinished) {
                val data = it.outputData
                val message = data.getString(UploadWorker.KEY_WORKER)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    //parallelWorker
    private fun setOneTimeWorkRequest4() {
        val workManager: WorkManager = WorkManager.getInstance(applicationContext)
        val data: Data = Data.Builder()
            .putInt(KEY_COUNT_VALUE, 125)
            .build()

        //Constraints - NetworkType.CONNECTED
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val uploadRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data)
            .build()

        val filteringRequest = OneTimeWorkRequest.Builder(FilteringWorker::class.java)
            .build()
        val compressingRequest = OneTimeWorkRequest.Builder(CompressWorker::class.java)
            .build()

        val downloadRequest = OneTimeWorkRequest.Builder(DownloadingWorker::class.java)
            .build()

        val parallelWorker = mutableListOf<OneTimeWorkRequest>()
        parallelWorker.add(downloadRequest)
        parallelWorker.add(filteringRequest)

        workManager
            .beginWith(parallelWorker)
            .then(compressingRequest)
            .then(uploadRequest)
            .enqueue()

        //Get Status Update
        workManager.getWorkInfoByIdLiveData(uploadRequest.id).observe(this, Observer {
            textView.text = it.state.name.toString()

            if (it.state.isFinished) {
                val data = it.outputData
                val message = data.getString(UploadWorker.KEY_WORKER)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    //PeriodicWorkRequest
    private fun setPeriodicWorkrequest() {
        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            DownloadingWorker::class.java, 16, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(periodicWorkRequest)

    }

    companion object {
        const val KEY_COUNT_VALUE = "key_count"
    }

}
