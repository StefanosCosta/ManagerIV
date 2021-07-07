package com.example.inventorymanagement2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventorymanagement2.databinding.ActivityMainBinding
import com.example.inventorymanagement2.databinding.HomeViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class HomeViewActivity : AppCompatActivity() {

    private lateinit var sessionManager : SessionManager
    private lateinit var binding: HomeViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        sessionManager.checkLogin()
        binding.logoutbtn.setOnClickListener {
//            binding
            CoroutineScope(Dispatchers.IO).launch {
//                            sendLoginRequest()
                val result =logout()
                withContext(Dispatchers.Main){
                    processResult(result)
                    sessionManager.logout()
                }
            }
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

    private fun processResult(result : String) {
//        withContext(Dispatchers.Main){
        if (result == "Log Out Success") {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            val t = Toast.makeText(this, result, Toast.LENGTH_SHORT)
            t.show()
            println(result)
        }
    }
    private fun logout() : String {
        var result = ""
        try{
            val url = URL("http://192.168.1.109/LoginRegister/logout.php")
            val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.setRequestMethod("POST")
            httpURLConnection.setDoOutput(true)
            httpURLConnection.setDoInput(true)
            val ops : OutputStream = httpURLConnection.getOutputStream()
            val writer : Writer = BufferedWriter( OutputStreamWriter(ops,"UTF-8"))
//                val out: Writer = BufferedWriter(OutputStreamWriter(System.out))

            var data:String = ""
            val params: MutableList<String> = ArrayList()
//            params.add(firstName)
//            params.add(lastName)
//            params.add(email)
//            params.add(password)
            val fields: MutableList<String> = ArrayList()
//            fields.add("name")
//            fields.add("surname")
//            fields.add("email")
//            fields.add("password")
            for (i in params.indices)
            {
                data += URLEncoder.encode(fields[i],"UTF-8") + "=" + URLEncoder.encode(params[i],"UTF-8") + "&"
            }
            writer.write(data)
            writer.flush()
            writer.close()
            ops.close()
            val ips : InputStream = httpURLConnection.getInputStream()
            val reader : BufferedReader = BufferedReader( InputStreamReader(ips,"ISO-8859-1"))
//        var line = ""
//        var result =""
            val text: List<String> = reader.readLines()
            for (line in text) {
                result += line
            }

            reader.close()
            ips.close()
            httpURLConnection.disconnect()
        }
        catch (e : IOException)
        {
            result = e.message.toString()
        }
        return result
    }

}