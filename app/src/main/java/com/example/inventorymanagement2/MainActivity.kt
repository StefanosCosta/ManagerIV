package com.example.inventorymanagement2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import com.example.inventorymanagement2.databinding.ActivityMainBinding
//import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import android.widget.Toast as Toast


class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager : SessionManager
//    private val URL_LOGIN = "http://192.168.1.119/LoginRegister/login.php"
    private val URL_LOGIN = "http://192.168.1.109/LoginRegister/login.php"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)
//        binding.SignIn.text = "Login"
        binding.registerhere.setOnClickListener {
            startActivity(Intent(this,Reroute_to_login::class.java))
        }
        binding.SignIn.setOnClickListener {
//            val userEmail = "steven@gmail.com"
//            val userPassword = "1234"
//            System.out.println()
//            binding.SignIn.text = binding.editTextTextEmailAddress.text
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()
            val isValid = validate(email, password)
            if (isValid) {
//                if (email == userEmail && password == userPassword) {
                    //                binding.SignIn.text = "Correct Login"
                        CoroutineScope(IO).launch {
//                            sendLoginRequest()
                            val result = verify(email,password)
                            println("reached here")
                            withContext(Main){
                                processResult(result)
                            }
                        }

//
//                    this.finish()
                }
                else
                {
                    var t = Toast.makeText(this,"Invalid email or Password",Toast.LENGTH_SHORT)
                    t.show()
                }
            }
//            else {binding.SignIn.text = "Sign In"}
        }
//        ActivityMainBinding
//    }
//    private suspend fun sendLoginRequest(){
//        delay(1000)
//
//    }
//private fun

    private fun processResult(result : JSONObject)
    {
//        withContext(Dispatchers.Main){
            if (result.has("login"))
            {
                if (result["login"] == true)
                {
                    println("reached here")
                    sessionManager.createSession(result["sessionID"] as String?)
                    startActivity(Intent(this, HomeViewActivity::class.java))
                }

                else{
                    var ti = Toast.makeText(this,result["message"] as String?,Toast.LENGTH_SHORT)
                    ti.show()
                    println(result)
                }
            }
            else
            {
                println(result.toString())
            }

//        }
    }

private fun verify(email : String, password: String) : JSONObject {
    val url = URL(URL_LOGIN)
//    try {}
    var result = ""
    var json : JSONObject
    val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
    try {

        httpURLConnection.setRequestMethod("POST")
        httpURLConnection.setDoInput(true)
        httpURLConnection.setDoOutput(true)
        httpURLConnection.setRequestProperty("Accept","application/json")

        val ops: OutputStream = httpURLConnection.getOutputStream()
        val writer: Writer = BufferedWriter(OutputStreamWriter(ops, "UTF-8"))
//                val out: Writer = BufferedWriter(OutputStreamWriter(System.out))

        var data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(
            email,
            "UTF-8"
        ) + "&&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(
            password,
            "UTF-8"
        )

//    val params: MutableList<String> = ArrayList()
//    params.add(email)
//    params.add(password)
//    val fields: MutableList<String> = ArrayList()
//    fields.add("email")
//    fields.add("password")
//    for (i in params.indices)
//    {
//        data += URLEncoder.encode(fields[i],"UTF-8") + "=" + URLEncoder.encode(params[i],"UTF-8") + "&"
//    }
        writer.write(data)
        writer.flush()
        writer.close()
        ops.close()
//        println("HERE")
        println(httpURLConnection.responseCode)
        val ips: InputStream = httpURLConnection.getInputStream()
        val reader = BufferedReader(InputStreamReader(ips, "ISO-8859-1"))
//    var line = ""
//        result = ""
//        println("HEREE")
        val text: List<String> = reader.readLines()
        for (line in text) {
            result += line
        }
        println(result)
//    while (line != null) {
//        line = reader.readLine()
//        result += line
//    }
//        val json = Gson()
        json = JSONObject(result)


        reader.close()
        ips.close()
        httpURLConnection.disconnect()
    }
    catch (e : IOException)
    {
//        result = JSONObject(e.message.toString() )
        result = e.message.toString()
        println(result)
        result = e.printStackTrace().toString()
        val ips: InputStream = httpURLConnection.errorStream
        val reader = BufferedReader(InputStreamReader(ips, "ISO-8859-1"))
//    var line = ""
//        result = ""
        var error = ""
//        println("HEREE")
        val text: List<String> = reader.readLines()
        for (line in text) {
            error += line
        }
        println(error)
        json = JSONObject("{" +"exception=" + "'" + result+"'" + "}")
        return json
//        result= JSONObject(result)
    }

    return json
}

private fun validate(email: String, password: String) : Boolean {
    var isValid = true
    if (isEmpty(email)) {
        binding.editTextTextEmailAddress.setError("Email Field cannot be empty")
        isValid = false
    } else {
        if (!isEmail(email)) {
            binding.editTextTextEmailAddress.setError("The email you entered is not a valid email")
            isValid = false
        }
    }
    if (isEmpty(password)) {
        binding.editTextTextPassword.setError("Password field cannot be empty")
        isValid = false
    } else {
        if (password.length < 4) {
            binding.editTextTextPassword.setError("Password must be at least 4 characters long")
            isValid = false
        }
    }

    return isValid
}



    
    private fun isEmail(text:String): Boolean
    {
        val email: CharSequence = text
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }
    private fun isEmpty(text: String): Boolean {
        val str: CharSequence = text
                return TextUtils.isEmpty(str)
                
    }
}