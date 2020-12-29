package com.nristekk.apps.architecturelab

import android.content.DialogInterface
import android.content.Intent
import android.icu.lang.UCharacter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.navigation.NavigationView
import com.nristekk.apps.architecturelab.database.items.User
import com.nristekk.apps.architecturelab.database.task.UserViewModel
import com.nristekk.apps.architecturelab.database.task.UserViewModelFactory
import com.nristekk.apps.architecturelab.ui.adapter.UsersAdapter
import com.nristekk.apps.architecturelab.ui.fragment.FragmentAddUser
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*


class ActivityLab : AppCompatActivity(), FragmentAddUser.AddUserComm {


    /*
    * User ViewModel initialize part
    */
    private val userViewModel: UserViewModel by viewModels {
        /*
        * Class cast will be success only ApplicationLab was declared in Manifest.xml file. or else you will face with Class cast exception
        * Android considerably accept another Application class-children-instance only when it declared in Manifest file
        */
        UserViewModelFactory((application as ApplicationLab).userRepos)
    }

    /*
    * for Rx disposable
    */
    private val compositDispos = CompositeDisposable()


    /*
    * for caching Users List
    */
    private var mUserList:List<User>? = null


    /*
    * View & Layouts elements part
    */
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var topToolbar: Toolbar
    private lateinit var navigatorView:NavigationView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var menuMore:ImageButton
    private lateinit var userListView:RecyclerView



    /*
    * Activity create View and Layout overhere
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab)

        //declare Drawer
        drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)

        //declare coordinate layout
        coordinatorLayout = findViewById<CoordinatorLayout>(R.id.coordinatorLayout)

        //declare action bar (top-toolbar) and set its initial properties
        topToolbar = findViewById<Toolbar>(R.id.topToolbar)
        topToolbar.setNavigationIcon(R.drawable.drawer_dark)
        setSupportActionBar(topToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        //declare NavigationView
        navigatorView = findViewById<NavigationView>(R.id.navigatorView)
        navigatorView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()
            when(it.itemId){
                R.id.navigator_developer_page-> navigateDeveloperPage()
                R.id.navigator_medium_dot_com -> navigateMediumDotCom()
            }
            true
        }


        //declare SwipeRefreshLayout
        swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener{
            /*
            *When refreshLayout had been swiped then trigger the task
            *->getting User List
            */
            getUserList()

        }


        //declare ImageButton menuMore
        menuMore = findViewById<ImageButton>(R.id.menuMore)
        val popupMenu = PopupMenu(this, menuMore)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)


        //Menu more to get Popup clicked event
        menuMore.setOnClickListener {
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.addUser -> addUserProcess()
                    R.id.clearUser -> clearUserProcess()
                    R.id.emailToLowerCase -> emailToLowerCaseProcess()
                    R.id.emailToUpperCase -> emailToUpperCaseProcess()
                    R.id.everyoneGettingOlder -> everyoneOlderProcess()
                }
                true
            }
            popupMenu.show()
        }


        //Declare List View (Recycler View)
        userListView = findViewById<RecyclerView>(R.id.usersListView)



        //..
        //..
        //..

    }


    /*
    * Activity will be in Resume, Visible state
    */
    override fun onResume() {
        super.onResume()
        getUserList()

    }


    /*
    * Our main task is to getting user list here
    */
    //Getting User List
    fun getUserList(){

        userViewModel.getAllObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object:Observer<List<User>>{
                        override fun onSubscribe(d: Disposable) {
                            compositDispos.add(d)
                            swipeRefreshLayout.isRefreshing = true

                        }
                        override fun onNext(list: List<User>) {

                            //Storing user List cached
                            mUserList = list

                            val userAdapter = UsersAdapter(list)
                            val linearLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
                            val itemDecor = DividerItemDecoration(applicationContext, RecyclerView.VERTICAL)
                            userListView.layoutManager = linearLayoutManager
                            userListView.addItemDecoration(itemDecor)
                            userListView.adapter = userAdapter

                            swipeRefreshLayout.isRefreshing = false
                        }
                        override fun onComplete() {swipeRefreshLayout.isRefreshing = false}
                        override fun onError(e: Throwable) {
                            Log.d(getString(R.string.logged), e.message?:"Some error occured")
                            swipeRefreshLayout.isRefreshing = false
                        }
                    }
                )

    }


    //Trigger User Adding process
    fun addUserProcess(){
        val addUserFragmt = FragmentAddUser()
        addUserFragmt.show(supportFragmentManager, null)
    }


    //Callback from AddUser Fragment, User as input parameter
    override fun insertUser(user: User?) {
        user?.let { userViewModel.insert(it) }
    }


    //Trigger viewModel to deleteAll Users
    fun clearUserProcess(){
        userViewModel.deleteAll()
    }


    //Trigger Email to lower case Transforming process
    fun emailToLowerCaseProcess(){

        val dialogBuilder:AlertDialog.Builder? = AlertDialog.Builder(this)
        dialogBuilder?.setMessage(R.string.email_to_lowercase_dialog_msg)
                     ?.setPositiveButton(R.string.ok, DialogInterface.OnClickListener {
                            dialog, which ->

                            CoroutineScope(SupervisorJob()).launch {

                                //Get user lise from ViewModel's suspend method.
                                val userlist:List<User> = userViewModel.getAllCoroutines()

                                //lowerMailOps is a Function type, making each user's email to be lowercase
                                val lowerMailOps:(User)->(User) = {
                                    it.email = it.email.toLowerCase()
                                    it
                                }

                                //Place user list and operation into ViewModel
                                val resultList = userViewModel.transformList(userlist,lowerMailOps)
                                userViewModel.updateUsers(resultList)

                                //If you wouldn't like to update the whole list at once
                                //For safety, You may do it in a loop fashion
                                /*
                                resultList.forEach {
                                    userViewModel.update(it)
                                }
                                */
                            }

                      })
                      ?.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener{
                            dialog, which -> Log.d(getString(R.string.logged),"Cancel was clicked")
                      })
        val dialog: AlertDialog? = dialogBuilder?.create()
        dialog?.show()


    }


    //Trigger Email to upper cae Transforming proces
    fun emailToUpperCaseProcess(){
        val dialogBuilder:AlertDialog.Builder? = AlertDialog.Builder(this)
        dialogBuilder?.setMessage(R.string.email_to_uppercase_dialog_msg)
                ?.setPositiveButton(R.string.ok, DialogInterface.OnClickListener{
                    dialog, which ->

                    CoroutineScope(SupervisorJob()).launch {

                        //Get user lise from ViewModel's suspend method.
                        val userlist:List<User> = userViewModel.getAllCoroutines()

                        //lowerMailOps is a Function type, making each user's email to be upperCase
                        val upperMailOps:(User)->(User) = {
                            it.email = it.email.toUpperCase()
                            it
                        }

                        //Place user list and operation into ViewModel
                        val resultList = userViewModel.transformList(userlist,upperMailOps)
                        userViewModel.updateUsers(resultList)

                    }

                })
                ?.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener{
                    dialog, which -> Log.d(getString(R.string.logged),"Cancel was clicked")
                })
        val dialog: AlertDialog? = dialogBuilder?.create()
        dialog?.show()
    }


    //Make every user one year older
    fun everyoneOlderProcess(){
        val dialogBuilder:AlertDialog.Builder? = AlertDialog.Builder(this)
        dialogBuilder?.setMessage(R.string.everyone_older_dialog_msg)
                ?.setPositiveButton(R.string.ok, DialogInterface.OnClickListener{
                    dialog, which ->

                    CoroutineScope(SupervisorJob()).launch {

                        //Get user lise from ViewModel's suspend method.
                        val userlist:List<User> = userViewModel.getAllCoroutines()

                        //lowerMailOps is a Function type, making each user's age getting 1 year older
                        val oneYearOlder:(User)->(User) = {
                            it.age = it.age + 1
                            it
                        }

                        //Place user list and operation into ViewModel
                        val resultList = userViewModel.transformList(userlist,oneYearOlder)
                        userViewModel.updateUsers(resultList)

                    }


                })
                ?.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener{
                    dialog, which -> Log.d(getString(R.string.logged),"Cancel was clicked")
                })
        val dialog: AlertDialog? = dialogBuilder?.create()
        dialog?.show()
    }


    // this launched when Drawer Navigator had been clicked
    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(GravityCompat.START);
        return super.onSupportNavigateUp()
    }


    // Navigate to Android Developer Page
    fun navigateDeveloperPage(){
        val url = "https://play.google.com/store/apps/dev?id=8835757637067754728"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }


    // Navigate to Medium.com
    fun navigateMediumDotCom(){
        val url = "https://medium.com/@rattayork"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }


    // When User press 'Back' Button
    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed()
        }

    }

    /*
    * Activity is in Background of others/another Activities or App
    * but still Visible
    */
    override fun onPause() {
        super.onPause()
        compositDispos.clear()
    }

    /*
    * Activity is in Background of others/another Activities or App
    * and inVisible
    */
    override fun onStop() {
        super.onStop()
        compositDispos.clear()
    }

    /*
    * Short period or moment that Activity is going to be destroyed
    */
    override fun onDestroy() {
        super.onDestroy()
        compositDispos.dispose()
    }




    //..
    //..
    //..

}