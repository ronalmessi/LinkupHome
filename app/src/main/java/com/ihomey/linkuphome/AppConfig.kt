package com.ihomey.linkuphome

import com.ihomey.linkuphome.data.vo.RGB

class AppConfig {

    companion object {

        val DEVICE_MODEL_NAME = arrayListOf("M1", "C3", "R2", "A2", "N1", "", "V1", "S1", "S2", "T1", "V2")
        val DEVICE_NAME = arrayListOf(R.string.title_lamp_m1, R.string.title_lamp_c3, R.string.title_lamp_r2, R.string.title_lamp_a2, R.string.title_lamp_n1, R.string.title_lamp_m1, R.string.title_lamp_v1, R.string.title_lamp_s1, R.string.title_lamp_s2, R.string.title_lamp_t1, R.string.title_lamp_v2)
        val DEVICE_ICON = arrayListOf(R.mipmap.ic_lamp_m1, R.mipmap.ic_lamp_c3, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_n1, R.mipmap.ic_lamp_m1, R.mipmap.ic_lamp_v1, R.mipmap.ic_lamp_s1_s2, R.mipmap.ic_lamp_s1_s2, R.mipmap.ic_lamp_t1, R.mipmap.ic_lamp_v1)
        val ROOM_ICON = arrayListOf(R.mipmap.ic_zone_bed_room, R.mipmap.ic_zone_living_room, R.mipmap.ic_zone_dining_room, R.mipmap.ic_zone_kitchen, R.mipmap.ic_zone_bathroom, R.mipmap.ic_zone_balcony, R.mipmap.ic_zone_corridor, R.mipmap.ic_zone_entrance, R.mipmap.ic_zone_garage, R.mipmap.ic_zone_garden, R.mipmap.ic_zone_office, R.mipmap.ic_zone_bar_counter, R.mipmap.ic_zone_deck, R.mipmap.ic_zone_tv_wall, R.mipmap.ic_zone_other)
        val LANGUAGE: Array<String> = arrayOf("en", "zh-rCN", "fr", "de", "es", "nl", "zh-rTW", "pt", "it", "ja", "ru", "ar", "da", "sv", "pl")

//        val API_SERVER = "http://api.linkuphome.net/"

        val API_SERVER = "http://linkuphome.xsmart.top/api/"

        val INSTRUCTIONS_BASE_URL = "http://app-manual.linkuphome.net/"
        val RESET_DEVICE_BASE_URL = "http://app-manual.linkuphome.net/resetsoft/resetsoft_"
        val FAQ_BASE_URL = "http://app-docs.linkuphome.net/#/FAQ/"

        val USER_AGGREEMENT_BASE_URL = "http://app-docs.linkuphome.net/#/UserAgreement/"
        val PRIVACY_STATEMENt_BASE_URL = "http://app-docs.linkuphome.net/#/PrivacyStatement/"

        val APP_SECRET = "f374cfda69e064322a2be320da6caf96"

        val REQUEST_BT_CODE = 102

        val REQUEST_CODE_OPEN_GPS= 104


        val RING_LIST = listOf(R.string.title_ring_0, R.string.title_ring_1, R.string.title_ring_2, R.string.title_ring_3)

        val DAY_OF_WEEK = listOf(R.string.title_every_sun, R.string.title_every_mon, R.string.title_every_tue, R.string.title_every_wed, R.string.title_every_thur, R.string.title_every_fri, R.string.title_every_sat)

        val BATTERY_LEVEL_ICON = intArrayOf(R.mipmap.ic_battery0, R.mipmap.ic_battery1, R.mipmap.ic_battery2, R.mipmap.ic_battery3, R.mipmap.ic_battery4, R.mipmap.ic_battery5, R.mipmap.ic_battery6)
        val RGB_COLOR_POSITION = arrayOf("13", "12", "14", "15", "17", "16", "01", "00", "02", "03", "05", "04", "07", "06", "08", "09", "0B", "0A", "0D", "0C", "0E", "0F", "11", "10")

        const val TIME_MS = 4 * 1000L

        val RGB_COLOR = listOf(
                RGB(29, 32, 131),
                RGB(95, 25, 129),
                RGB(0, 70, 151),
                RGB(0, 104, 177),
                RGB(0, 155, 228),
                RGB(0, 129, 204),
                RGB(0, 153, 145),
                RGB(0, 154, 188),
                RGB(0, 150, 106),
                RGB(0, 148, 68),
                RGB(137, 190, 31),
                RGB(0, 255, 0),
                RGB(255, 236, 0),
                RGB(202, 214, 0),
                RGB(247, 194, 0),
                RGB(238, 146, 0),
                RGB(255, 0, 0),
                RGB(230, 96, 0),
                RGB(224, 0, 79),
                RGB(225, 0, 50),
                RGB(224, 0, 105),
                RGB(223, 0, 127),
                RGB(141, 7, 128),
                RGB(185, 0, 128))
    }
}