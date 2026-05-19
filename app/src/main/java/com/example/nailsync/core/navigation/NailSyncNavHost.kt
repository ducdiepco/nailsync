package com.example.nailsync.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nailsync.features.checkout.CheckoutScreen
import com.example.nailsync.features.customer.CustomerDetailScreen
import com.example.nailsync.features.customer.CustomerListScreen
import com.example.nailsync.features.queue.QueueScreen
import com.example.nailsync.features.technician.TechnicianAssignmentScreen
import com.example.nailsync.features.ticket.AddServiceScreen
import com.example.nailsync.features.ticket.TicketDetailScreen

@Composable
fun NailSyncNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Routes.QUEUE) {
        composable(Routes.QUEUE) {
            QueueScreen(
                onTicketClick = { ticketId ->
                    navController.navigate(Routes.ticketDetail(ticketId))
                },
                onCustomersClick = {
                    navController.navigate(Routes.CUSTOMER_LIST)
                }
            )
        }
        composable(
            route = Routes.TICKET_DETAIL,
            arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
        ) {
            TicketDetailScreen(
                onBack = { navController.popBackStack() },
                onAddService = { ticketId ->
                    navController.navigate(Routes.addService(ticketId))
                },
                onAssignTechnician = { serviceId ->
                    navController.navigate(Routes.technicianAssignment(serviceId))
                },
                onCheckout = { ticketId ->
                    navController.navigate(Routes.checkout(ticketId))
                }
            )
        }
        composable(
            route = Routes.ADD_SERVICE,
            arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
        ) {
            AddServiceScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = Routes.TECHNICIAN_ASSIGNMENT,
            arguments = listOf(navArgument("serviceId") { type = NavType.IntType })
        ) {
            TechnicianAssignmentScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = Routes.CHECKOUT,
            arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
        ) {
            CheckoutScreen(
                onBack = { navController.popBackStack() },
                onPaymentComplete = {
                    navController.navigate(Routes.QUEUE) {
                        popUpTo(Routes.QUEUE) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.CUSTOMER_LIST) {
            CustomerListScreen(
                onBack = { navController.popBackStack() },
                onCustomerClick = { customerId ->
                    navController.navigate(Routes.customerDetail(customerId))
                },
                onNewCustomer = {
                    navController.navigate(Routes.newCustomer())
                }
            )
        }
        composable(
            route = Routes.CUSTOMER_DETAIL,
            arguments = listOf(navArgument("customerId") { type = NavType.IntType })
        ) {
            CustomerDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
