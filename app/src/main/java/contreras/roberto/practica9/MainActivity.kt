package contreras.roberto.practica9

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator

class MainActivity : AppCompatActivity() {
    private val userRef = FirebaseDatabase.getInstance().getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var btnSave: Button = findViewById(R.id.button) as Button
        btnSave.setOnClickListener{saveMarkFromForm()}


        userRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val usuario = parseUserFromString(snapshot.value.toString())
                if (usuario != null) writeMark(usuario)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })

    }

    private fun saveMarkFromForm() {
        var name: EditText = findViewById(R.id.editTextText) as EditText
        var lastName: EditText = findViewById(R.id.editTextText2) as EditText
        var age: EditText = findViewById(R.id.editTextText3) as EditText

        val usuario = Users (
            name.text.toString(),
            lastName.text.toString(),
            age.text.toString(),
        )
        userRef.push().setValue(usuario)
    }

    private fun writeMark(mark: Users) {
        val listV: TextView = findViewById(R.id.textView) as TextView
        val text = listV.text.toString() + mark.toString() + "\n"
        listV.text = text
    }

    fun parseUserFromString(data: String): Users? {
        return try {
            val cleanedData = data.removePrefix("{").removeSuffix("}")
            val keyValuePairs = cleanedData.split(", ").associate {
                val (key, value) = it.split("=")
                key.trim() to value.trim()
            }

            Users(
                firstName = keyValuePairs["firstName"],
                lastName = keyValuePairs["lastName"],
                age = keyValuePairs["age"]
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}