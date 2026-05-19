package com.example.nailsync.data.seeder

import com.example.nailsync.data.local.dao.ServiceDao
import com.example.nailsync.data.local.dao.TechnicianDao
import com.example.nailsync.data.local.entity.ServiceEntity
import com.example.nailsync.data.local.entity.TechnicianEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSeeder @Inject constructor(
    private val technicianDao: TechnicianDao,
    private val serviceDao: ServiceDao
) {
    suspend fun seed() {
        if (technicianDao.count() == 0) {
            technicianDao.insertAll(
                listOf(
                    TechnicianEntity(name = "Amy"),
                    TechnicianEntity(name = "Lisa"),
                    TechnicianEntity(name = "Maria"),
                    TechnicianEntity(name = "Cindy"),
                    TechnicianEntity(name = "Jenny"),
                    TechnicianEntity(name = "Tracy"),
                    TechnicianEntity(name = "Ruby"),
                    TechnicianEntity(name = "Michelle"),
                )
            )
        }
        if (serviceDao.count() == 0) {
            serviceDao.insertAll(nailServices + waxingServices + facialServices + massageServices)
        }
    }

    private val nailServices = listOf(
        ServiceEntity(name = "Manicure", price = 25.0, durationMinutes = 30, category = "NAILS"),
        ServiceEntity(name = "Pedicure", price = 35.0, durationMinutes = 45, category = "NAILS"),
        ServiceEntity(name = "Gel Manicure", price = 40.0, durationMinutes = 45, category = "NAILS"),
        ServiceEntity(name = "Gel Pedicure", price = 50.0, durationMinutes = 60, category = "NAILS"),
        ServiceEntity(name = "Acrylic Full Set", price = 55.0, durationMinutes = 75, category = "NAILS"),
        ServiceEntity(name = "Acrylic Fill", price = 35.0, durationMinutes = 45, category = "NAILS"),
        ServiceEntity(name = "Pink & White Full Set", price = 65.0, durationMinutes = 90, category = "NAILS"),
        ServiceEntity(name = "Dip Powder", price = 45.0, durationMinutes = 50, category = "NAILS"),
        ServiceEntity(name = "French Tips", price = 10.0, durationMinutes = 15, category = "NAILS"),
        ServiceEntity(name = "Nail Art (per nail)", price = 5.0, durationMinutes = 10, category = "NAILS"),
        ServiceEntity(name = "Nail Removal", price = 15.0, durationMinutes = 20, category = "NAILS"),
        ServiceEntity(name = "Mani & Pedi Combo", price = 55.0, durationMinutes = 70, category = "NAILS"),
        ServiceEntity(name = "Gel Mani & Pedi Combo", price = 80.0, durationMinutes = 90, category = "NAILS"),
    )

    private val waxingServices = listOf(
        ServiceEntity(name = "Eyebrow Wax", price = 12.0, durationMinutes = 15, category = "WAXING"),
        ServiceEntity(name = "Lip Wax", price = 8.0, durationMinutes = 10, category = "WAXING"),
        ServiceEntity(name = "Chin Wax", price = 8.0, durationMinutes = 10, category = "WAXING"),
        ServiceEntity(name = "Full Face Wax", price = 30.0, durationMinutes = 30, category = "WAXING"),
        ServiceEntity(name = "Underarm Wax", price = 20.0, durationMinutes = 15, category = "WAXING"),
        ServiceEntity(name = "Half Leg Wax", price = 35.0, durationMinutes = 30, category = "WAXING"),
        ServiceEntity(name = "Full Leg Wax", price = 55.0, durationMinutes = 45, category = "WAXING"),
        ServiceEntity(name = "Bikini Wax", price = 35.0, durationMinutes = 30, category = "WAXING"),
    )

    private val facialServices = listOf(
        ServiceEntity(name = "Basic Facial", price = 50.0, durationMinutes = 45, category = "FACIAL"),
        ServiceEntity(name = "Deep Cleansing Facial", price = 70.0, durationMinutes = 60, category = "FACIAL"),
        ServiceEntity(name = "Anti-Aging Facial", price = 85.0, durationMinutes = 60, category = "FACIAL"),
        ServiceEntity(name = "Microdermabrasion", price = 95.0, durationMinutes = 60, category = "FACIAL"),
    )

    private val massageServices = listOf(
        ServiceEntity(name = "30 Min Swedish", price = 45.0, durationMinutes = 30, category = "MASSAGE"),
        ServiceEntity(name = "60 Min Swedish", price = 75.0, durationMinutes = 60, category = "MASSAGE"),
        ServiceEntity(name = "90 Min Swedish", price = 100.0, durationMinutes = 90, category = "MASSAGE"),
        ServiceEntity(name = "60 Min Deep Tissue", price = 85.0, durationMinutes = 60, category = "MASSAGE"),
        ServiceEntity(name = "Hot Stone Massage", price = 90.0, durationMinutes = 60, category = "MASSAGE"),
        ServiceEntity(name = "Couples Massage", price = 160.0, durationMinutes = 60, category = "MASSAGE"),
    )
}
