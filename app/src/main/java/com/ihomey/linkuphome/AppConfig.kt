package com.ihomey.linkuphome

class AppConfig {

    companion object {

        val DEVICE_MODEL_NAME = arrayListOf("M1", "N1", "A2", "R2", "C3", "V1", "S1", "S2")
        val DEVICE_NAME = arrayListOf(R.string.lamp_outdoor, R.string.lamp_rgb, R.string.lamp_cct, R.string.lamp_r2, R.string.lamp_c3, R.string.lamp_s1, R.string.lamp_s2, R.string.lamp_s2)
        val DEVICE_ICON = arrayListOf(R.mipmap.ic_lamp_m1, R.mipmap.ic_lamp_n1, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_r2_a2, R.mipmap.ic_lamp_c3, R.mipmap.ic_lamp_v1, R.mipmap.ic_lamp_s1_s2, R.mipmap.ic_lamp_s1_s2)
        val ROOM_ICON = arrayListOf(R.mipmap.ic_zone_bed_room, R.mipmap.ic_zone_living_room, R.mipmap.ic_zone_dining_room,R.mipmap.ic_zone_kitchen,  R.mipmap.ic_zone_bathroom,R.mipmap.ic_zone_balcony, R.mipmap.ic_zone_corridor, R.mipmap.ic_zone_entrance, R.mipmap.ic_zone_garage, R.mipmap.ic_zone_garden, R.mipmap.ic_zone_office, R.mipmap.ic_zone_bar_counter, R.mipmap.ic_zone_deck, R.mipmap.ic_zone_tv_wall, R.mipmap.ic_zone_other)
        val LANGUAGE: Array<String> = arrayOf("en", "zh", "fr", "de", "es", "nl")
        const val OSS_BUCKET_NAME = "mediabox"
    }


}