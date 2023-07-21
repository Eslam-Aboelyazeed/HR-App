package com.fyc.android.hrapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fyc.android.hrapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration : AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
//        drawerLayout = _binding.drawerLayout
//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//        val navController = this.findNavController(R.id.nav_host_fragment)
//        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
//        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
//        NavigationUI.setupWithNavController(_binding.navView, navController)

//        appBarConfiguration = AppBarConfiguration(setOf(R.id.workerFragment, R.id.inputFragment,
//            R.id.WDetailsFragment, R.id.calendarFragment, R.id.attendanceFragment,
//            R.id.ADetailsFragment, R.id.salaryFragment, R.id.salaryDetailsFragment,
//            R.id.adminFragment, R.id.adminInputFragment, R.id.adminDetailsFragment,
//            R.id.WAttendedDaysFragment), _binding.drawerLayout)

        setUpNavigation()

        val nc = this.findNavController(R.id.nav_host_fragment)

        _binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.workerFragment -> {
//                    nc.popBackStack()
//                    navController.clearBackStack(R.id.workerFragment)
//                    fragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                    navController.popBackStack(0,true)
//                    clearBackStack(fragmentManager)
                    val popBackStackOptions = NavOptions.Builder().setPopUpTo(R.id.workerFragment, false).build()
                    nc.navigate(R.id.workerFragment, null, popBackStackOptions)
//                    nc.popBackStack(R.id.workerFragment, false)
                    _binding.drawerLayout.close()}
                R.id.adminFragment -> {
//                    navController.clearBackStack(R.id.workerFragment)
//                    fragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                    navController.popBackStack(0,true)
//                    clearBackStack(fragmentManager)
                    val popBackStackOptions = NavOptions.Builder().setPopUpTo(R.id.adminFragment, false).build()
                    nc.navigate(R.id.adminFragment, null, popBackStackOptions)
//                    nc.popBackStack(R.id.adminFragment, false)
                    _binding.drawerLayout.close()}
                R.id.calendarFragment -> {
//                    navController.clearBackStack(R.id.workerFragment)
//                    fragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                    navController.popBackStack(0,true)
//                    clearBackStack(fragmentManager)
                    val popBackStackOptions = NavOptions.Builder().setPopUpTo(R.id.calendarFragment, false).build()
                    nc.navigate(R.id.calendarFragment, null, popBackStackOptions)
//                    nc.popBackStack(R.id.calendarFragment, false)
                    _binding.drawerLayout.close()
                }
                R.id.salaryFragment -> {
//                    navController.clearBackStack(R.id.workerFragment)
//                    fragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                    navController.popBackStack(0,true)
//                    clearBackStack(fragmentManager)
                    val popBackStackOptions = NavOptions.Builder().setPopUpTo(R.id.salaryFragment, false).build()
                    nc.navigate(R.id.salaryFragment, null, popBackStackOptions)
//                    nc.popBackStack(R.id.salaryFragment, false)
                    _binding.drawerLayout.close()}
                else -> {
//                    navController.clearBackStack(R.id.workerFragment)
//                    fragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                    navController.popBackStack(0,true)
//                    clearBackStack(fragmentManager)
                    val popBackStackOptions = NavOptions.Builder().setPopUpTo(R.id.workerFragment, false).build()
                    nc.navigate(R.id.workerFragment, null, popBackStackOptions)
//                    nc.popBackStack(R.id.workerFragment, false)
                    _binding.drawerLayout.close()}
            }
            true
        }
    }

//    override fun onSupportNavigateUp() =
//        navigateUp(findNavController(R.id.nav_host_fragment), _binding.drawerLayout)

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    private fun setUpNavigation() {
        val navController = findNavController(R.id.nav_host_fragment)

        setSupportActionBar(_binding.toolbar)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.workerFragment, R.id.inputFragment,
            R.id.WDetailsFragment, R.id.calendarFragment, R.id.attendanceFragment,
            R.id.ADetailsFragment, R.id.salaryFragment, R.id.salaryDetailsFragment,
            R.id.adminFragment, R.id.adminInputFragment, R.id.adminDetailsFragment,
            R.id.WAttendedDaysFragment), _binding.drawerLayout)

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

//        _binding.navView.setupWithNavController(navController)

        NavigationUI.setupWithNavController(_binding.navView, navController)

//        val navigationView = _binding.navView
//        navigationView.setNavigationItemSelectedListener { menuItem ->
//            // When an item in the Navigation Drawer is clicked, pop the back stack
//            navController.popBackStack(menuItem.itemId, false)
//            // Close the Navigation Drawer
//            _binding.drawerLayout.closeDrawer(GravityCompat.START)
//            true
//        }

//        val fragmentManager = supportFragmentManager

//        _binding.navView.setNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.workerFragment -> {
////                    navController.clearBackStack(R.id.workerFragment)
////                    fragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE)
////                    navController.popBackStack(0,true)
////                    clearBackStack(fragmentManager)
//                    navController.navigate(R.id.workerFragment)
//                    navController.popBackStack(R.id.workerFragment, false)
//                    _binding.drawerLayout.close()}
//                R.id.adminFragment -> {
////                    navController.clearBackStack(R.id.workerFragment)
////                    fragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE)
////                    navController.popBackStack(0,true)
////                    clearBackStack(fragmentManager)
//                    navController.navigate(R.id.adminFragment)
//                    navController.popBackStack(R.id.adminFragment, false)
//                    _binding.drawerLayout.close()}
//                R.id.calendarFragment -> {
////                    navController.clearBackStack(R.id.workerFragment)
////                    fragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE)
////                    navController.popBackStack(0,true)
////                    clearBackStack(fragmentManager)
//                    navController.navigate(R.id.calendarFragment)
//                    navController.popBackStack(R.id.calendarFragment, false)
//                    _binding.drawerLayout.close()
//                }
//                R.id.salaryFragment -> {
////                    navController.clearBackStack(R.id.workerFragment)
////                    fragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE)
////                    navController.popBackStack(0,true)
////                    clearBackStack(fragmentManager)
//                    navController.navigate(R.id.salaryFragment)
//                    navController.popBackStack(R.id.salaryFragment, false)
//                    _binding.drawerLayout.close()}
//                else -> {
////                    navController.clearBackStack(R.id.workerFragment)
////                    fragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE)
////                    navController.popBackStack(0,true)
////                    clearBackStack(fragmentManager)
//                    navController.navigate(R.id.workerFragment)
//                    navController.popBackStack(R.id.workerFragment, false)
//                    _binding.drawerLayout.close()}
//            }
//            true
//        }

        navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            val toolBar = supportActionBar ?: return@addOnDestinationChangedListener
            when (destination.id) {
                R.id.inputFragment -> {
                    toolBar.setDisplayShowTitleEnabled(true)
                    //_binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.workerFragment -> {
                    toolBar.setDisplayShowTitleEnabled(true)
                    //_binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.WDetailsFragment -> {
                    toolBar.setDisplayShowTitleEnabled(true)
                    //_binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.calendarFragment -> {
                    toolBar.setDisplayShowTitleEnabled(true)
                    //_binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.attendanceFragment -> {
                    toolBar.setDisplayShowTitleEnabled(true)
                    //_binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.ADetailsFragment -> {
                    toolBar.setDisplayShowTitleEnabled(true)
                    //_binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.salaryFragment -> {
                    toolBar.setDisplayShowTitleEnabled(true)
                    //_binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.salaryDetailsFragment -> {
                    toolBar.setDisplayShowTitleEnabled(true)
                    //_binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.adminFragment -> {
                    toolBar.setDisplayShowTitleEnabled(true)
                    //_binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.adminInputFragment -> {
                    toolBar.setDisplayShowTitleEnabled(true)
                    //_binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.adminDetailsFragment-> {
                    toolBar.setDisplayShowTitleEnabled(true)
                    //_binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.WAttendedDaysFragment -> {
                    toolBar.setDisplayShowTitleEnabled(true)
                    //_binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                R.id.loginFragment -> {
                    toolBar.hide()
                    _binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
            }

        }
    }

    fun clearBackStack(fragmentManager: FragmentManager) {
        if (fragmentManager.backStackEntryCount > 0) {
            val entry = fragmentManager.getBackStackEntryAt(0)
            fragmentManager.popBackStack(entry.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }
}