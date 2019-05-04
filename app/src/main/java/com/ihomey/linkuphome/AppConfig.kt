package com.ihomey.linkuphome

class AppConfig {

    companion object {
        val DEVICE_MODEL_NAME = arrayListOf("C3", "R2", "A2", "N1", "M1", "V1", "S1", "S2", "T1","V2")
        val DEVICE_NAME = arrayListOf(R.string.lamp_c3, R.string.lamp_r2, R.string.lamp_a2, R.string.lamp_n1, R.string.lamp_m1, R.string.lamp_v1, R.string.lamp_s1, R.string.lamp_s2, R.string.lamp_t1, R.string.lamp_v2)
        val DEVICE_ICON = arrayListOf(R.mipmap.ic_lamp_c3, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_n1, R.mipmap.ic_lamp_m1, R.mipmap.ic_lamp_v1, R.mipmap.ic_lamp_s1_s2, R.mipmap.ic_lamp_s1_s2,R.mipmap.ic_lamp_t1, R.mipmap.ic_lamp_v1)
        val ROOM_ICON = arrayListOf(R.mipmap.ic_zone_bed_room, R.mipmap.ic_zone_living_room, R.mipmap.ic_zone_dining_room, R.mipmap.ic_zone_kitchen, R.mipmap.ic_zone_bathroom, R.mipmap.ic_zone_balcony, R.mipmap.ic_zone_corridor, R.mipmap.ic_zone_entrance, R.mipmap.ic_zone_garage, R.mipmap.ic_zone_garden, R.mipmap.ic_zone_office, R.mipmap.ic_zone_bar_counter, R.mipmap.ic_zone_deck, R.mipmap.ic_zone_tv_wall, R.mipmap.ic_zone_other)
        val LANGUAGE: Array<String> = arrayOf("en", "cn", "fr", "de", "es", "nl","tw", "pt", "it", "ja", "ru","en", "da", "sv", "pl")

        val API_SERVER = "http://linkuphome.ray-joy.com/"
        val INSTRUCTIONS_URL = "http://ihomey.cc/guide/guide.html"
        val APP_SECRET = "f374cfda69e064322a2be320da6caf96"
    }


}