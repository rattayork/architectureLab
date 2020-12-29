package com.nristekk.apps.architecturelab.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.nristekk.apps.architecturelab.R
import com.nristekk.apps.architecturelab.database.task.UserViewModel
import androidx.activity.viewModels
import com.nristekk.apps.architecturelab.ActivityLab
import com.nristekk.apps.architecturelab.ApplicationLab
import com.nristekk.apps.architecturelab.database.items.User
import com.nristekk.apps.architecturelab.database.task.UserViewModelFactory
import kotlinx.coroutines.runBlocking

class FragmentAddUser : DialogFragment() {


    interface AddUserComm{
        fun insertUser(user: User?)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        //Get Dialog Builder
        val dialogBuilder:AlertDialog.Builder = AlertDialog.Builder(activity)

        //Get communication interface
        val addUserComm = try {activity as AddUserComm }
                                catch(ex:Throwable){null}

        //Get Layout Inflater
        val inflater: LayoutInflater = requireActivity().layoutInflater

        //Get Root View of Fragment
        val fragmentView: View = inflater.inflate(R.layout.fragment_adduser, null)

        //Get View Elements
        val inputFirstName = fragmentView.findViewById<EditText>(R.id.inputFirstName)
        val inputAge = fragmentView.findViewById<EditText>(R.id.inputAge)
        val inputLastName = fragmentView.findViewById<EditText>(R.id.inputLastName)
        val inputEmailAddr = fragmentView.findViewById<EditText>(R.id.inputEmailAddr)
        val inputTel = fragmentView.findViewById<EditText>(R.id.inputTel)
        val buttonOk = fragmentView.findViewById<TextView>(R.id.buttonOk)
        val buttonCancel = fragmentView.findViewById<TextView>(R.id.buttonCancel)

        //If 'Cancel' Textview clicked, close the Dialog
        buttonCancel.setOnClickListener{ dismiss() }

        //If 'Ok' Textview clicked, try to add user
        buttonOk.setOnClickListener{
            when{
                (inputFirstName.text.toString().isEmpty())-> Toast.makeText(activity, getString(R.string.first_name)+" "+getString(R.string.should_not_empty),Toast.LENGTH_SHORT).show()
                (inputLastName.text.toString().isEmpty())-> Toast.makeText(activity, getString(R.string.last_name)+" "+getString(R.string.should_not_empty),Toast.LENGTH_SHORT).show()
                (inputEmailAddr.text.toString().isEmpty())-> Toast.makeText(activity, getString(R.string.email_addr)+" "+getString(R.string.should_not_empty),Toast.LENGTH_SHORT).show()
                (inputAge.text.toString().isEmpty())-> Toast.makeText(activity, getString(R.string.age)+" "+getString(R.string.should_not_empty),Toast.LENGTH_SHORT).show()
                else-> {
                    val user = User(inputFirstName.text.toString(), inputLastName.text.toString(), inputAge.text.toString().toInt(), inputEmailAddr.text.toString(), inputTel.text.toString())
                    runBlocking {  addUserComm?.insertUser(user)  }
                    dismiss()
                }
            }


        }

        dialogBuilder.setView(fragmentView)

        return dialogBuilder.create()

    }


}