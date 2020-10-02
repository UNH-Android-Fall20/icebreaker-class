package edu.newhaven.icebreaker_class

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = javaClass.name

    private val db = FirebaseFirestore.getInstance()

    private lateinit var questionBank: MutableList<Question>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db.collection("questions")
            .get()
            .addOnSuccessListener { documents ->
                questionBank = mutableListOf()
                for (document in documents) {
                    val question = document.toObject(Question::class.java)
                    questionBank.add(question)
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
                
                // set the initial question
                lblQuestion.setText(questionBank.random().text)

                // setup event to generate a new question on demand
                btnQuestion.setOnClickListener {
                    lblQuestion.setText(questionBank.random().text)
                }

                // setup the submit button
                btnSubmit.setOnClickListener {
                    // add a new document with a generated id
                    val student = hashMapOf(
                        "firstname" to txtFirstName.text.toString(),
                        "lastname" to txtLastName.text.toString(),
                        "question" to lblQuestion.text.toString(),
                        "answer" to txtAnswer.text.toString()
                    )

                    db.collection("students")
                        .add(student)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error adding document", e)
                        }
                }
                
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        Log.i(TAG, "Pushing data to Firebase")
    }
}
