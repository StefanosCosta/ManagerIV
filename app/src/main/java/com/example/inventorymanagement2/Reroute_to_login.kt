package com.example.inventorymanagement2

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.inventorymanagement2.databinding.RegisterViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class Reroute_to_login : AppCompatActivity() {

    private lateinit var binding: RegisterViewBinding
    private val URL_SIGNUP = "http://192.168.1.109/LoginRegister/signup.php"
//    private val URL_SIGNUP = "http://192.168.1.119/LoginRegister/signup.php"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signinhere.setOnClickListener{
            startActivity(Intent(this,MainActivity::class.java))
        }
        binding.registerbtn.setOnClickListener {
            var firstName = binding.name.text.toString()
            var lastName = binding.surname.text.toString()
            var email = binding.email.text.toString()
            var password = binding.password.text.toString()
            var confirmPassword = binding.confirm.text.toString()
            if (validate(firstName,lastName,email,password,confirmPassword))
            {
                //Start ProgressBar first (Set visibility VISIBLE)
//                val url = URL("http://localhost:8080/LoginRegister/signup.php")
//                val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
//                httpURLConnection.setRequestMethod("POST")
//                httpURLConnection.setDoOutput(true)
//                httpURLConnection.setDoInput(true)
//                val ops : OutputStream = httpURLConnection.getOutputStream()
//                val writer : Writer = BufferedWriter( OutputStreamWriter(ops,"UTF-8"))
////                val out: Writer = BufferedWriter(OutputStreamWriter(System.out))
//
//                var data:String = ""
//                val params: MutableList<String> = ArrayList()
//                params.add(firstName)
//                params.add(lastName)
//                params.add(email)
//                params.add(password)
//                val fields: MutableList<String> = ArrayList()
//                fields.add("name")
//                fields.add("surname")
//                fields.add("email")
//                fields.add("password")
//                for (i in params.indices)
//                {
//                    data += URLEncoder.encode(fields[i],"UTF-8") + "=" + URLEncoder.encode(params[i],"UTF-8") + "&"
//                }
//                writer.write(data)
//                writer.flush()
//                writer.close()
//                ops.close()
//                val ips :InputStream  = httpURLConnection.getInputStream()
//                val reader : BufferedReader = BufferedReader( InputStreamReader(ips,"ISO-8859-1"))
//                var line = ""
//                var result =""
//                while (line != null) {
//                    line = reader.readLine()
//                    result += line
//                }
//
//                reader.close()
//                ips.close()
//                httpURLConnection.disconnect()
                CoroutineScope(Dispatchers.IO).launch {
//                            sendLoginRequest()
                    val result = verify(firstName,lastName,email,password)
                    withContext(Dispatchers.Main){
                        processResult(result)
                    }
                }
//                val verification = verify(firstName,lastName, email, password)
                 }
        }
    }

    private fun processResult(result : String) {
//        withContext(Dispatchers.Main){
        if (result == "Sign Up Success") {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            val t = Toast.makeText(this, result, Toast.LENGTH_SHORT)
            t.show()
            println(result)
        }
    }

    private fun verify(firstName : String, lastName: String, email : String, password: String) : String {
        var result = ""
        try{
        val url = URL("http://192.168.1.119/LoginRegister/signup.php")
        val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        httpURLConnection.setRequestMethod("POST")
        httpURLConnection.setDoOutput(true)
        httpURLConnection.setDoInput(true)
        val ops : OutputStream = httpURLConnection.getOutputStream()
        val writer : Writer = BufferedWriter( OutputStreamWriter(ops,"UTF-8"))
//                val out: Writer = BufferedWriter(OutputStreamWriter(System.out))

        var data:String = ""
        val params: MutableList<String> = ArrayList()
        params.add(firstName)
        params.add(lastName)
        params.add(email)
        params.add(password)
        val fields: MutableList<String> = ArrayList()
        fields.add("name")
        fields.add("surname")
        fields.add("email")
        fields.add("password")
        for (i in params.indices)
        {
            data += URLEncoder.encode(fields[i],"UTF-8") + "=" + URLEncoder.encode(params[i],"UTF-8") + "&"
        }
        writer.write(data)
        writer.flush()
        writer.close()
        ops.close()
        val ips :InputStream  = httpURLConnection.getInputStream()
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

    private fun validate(firstName :String, lastName: String,email: String, password: String, confirm : String) : Boolean {
        var isValid = true
        if (isEmpty(email)) {
            binding.email.setError("Email Field cannot be empty")
            isValid = false
        } else {
            if (!isEmail(email)) {
                binding.email.setError("The email you entered is not a valid email")
                isValid = false
            }
        }
        if (isEmpty(password)) {
            binding.password.setError("Password field cannot be empty")
            isValid = false
        }
        else{
            if (password.length < 4){
                binding.password.setError("Password must be at least 4 characters long")
                isValid = false
            }
            else if (password != confirm)
            {
                binding.confirm.setError("Confirm Password field must be equal to the password field")
                isValid = false
            }
        }
        if (isEmpty(firstName))
        {
            val t = Toast.makeText(this,"First Name Field cannot be empty", Toast.LENGTH_SHORT)
            t.show()
            isValid = false
        }
        if (isEmpty(lastName))
        {
            val t = Toast.makeText(this,"Last Name Field cannot be empty", Toast.LENGTH_SHORT)
            t.show()
            isValid = false
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