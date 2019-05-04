package com.ihomey.linkuphome

import java.io.File
import java.io.FileInputStream
import jxl.Workbook
import java.io.FileOutputStream


class StringResourceGenerator {

    companion object {

        private val fieldNameLists= listOf(
                listOf("splash_tip_agreement","splash_user_license_agreement","splash_corporate_privacy_statement","splash_action_start","error_network"),
                listOf("title_create_zone","action_save","msg_default_zone","tip_name_zone","home","title_device","msg_no_device","msg_add_device","title_zone","title_setting","action_delete","action_remove_device","action_cancel","action_confirm","title_choose_device_type","title_lamp_m1","title_lamp_n1","title_lamp_a2","title_lamp_r2","title_lamp_c3","title_lamp_v1","title_lamp_s2","title_lamp_s1","title_lamp_t1","msg_device_ready","msg_device_search_hint1","msg_device_search_hint2","action_next","title_reset_device","title_search_device","msg_device_reset_hint1","msg_device_reset_hint2","msg_device_reset_hint2","msg_device_reset_hint3","title_step1","msg_device_reset_hint4","msg_device_reset_hint4","title_step2","msg_device_reset_hint5","msg_device_reset_hint5","msg_device_reset_hint5","msg_device_reset_hint5","msg_device_reset_hint5","msg_device_reset_hint5","msg_device_reset_hint5","msg_device_searching","msg_notes","msg_search_device_fail","title_connect_device","action_connect_device","action_rename_device","title_rgb","title_cct","action_slow_speed","action_normal_speed","action_fast_speed"),
                listOf("msg_create_room","title_choose_room_type","title_rename_room","title_light_change_speed","title_delete_room","msg_delete_room","action_add_device","action_delete","title_available_devices","action_save","title_save_modifications","msg_save_modifications","msg_add_device_for_room"),
                listOf("title_current_zone","title_language","title_user_manual","title_faqs","title_more","title_user_agreement","title_private_statement","title_share_zone","title_join_zone","title_rename","title_invitation_code","msg_share_zone_hint1","msg_share_zone_hint2","msg_share_zone_hint3","msg_join_zone_hint1","msg_join_zone_hint2","msg_share_zone_hint4","title_error_invitation_code","msg_error_invitation_code","msg_delete_zone_hint","action_go_to_devices","msg_minimum_zone","action_join","title_quit_zone","msg_quit_shared_zone"),
                listOf("title_scene_mode","title_scene_mode_read","title_scene_mode_sunset","title_scene_mode_rest","title_scene_mode_spring","title_scene_mode_rainforest"),
                listOf("title_scene_mode_flow","title_scene_mode_seek","title_scene_mode_surf","title_scene_mode_rainbow","title_scene_mode_star"),
                listOf("msg_device_disconnected","title_language_setting","msg_device_connected","msg_device_connect_failed","title_scene_mode_lighting","msg_device_connecting","title_lamp_v2","msg_join_zone_success")
        )

        private val languageList= listOf("zh-rCN","en","fr","de","es","nl","zh-rTW","pt","it","ja","ru","gg","da","sv","pl")


        @JvmStatic
        fun main(args: Array<String>) {
            val file = File("/Users/dongcaizheng/Desktop/language.xls")
            if(file.isFile&&file.exists()){
                val inputStream =FileInputStream(file)
                val workBook = Workbook.getWorkbook(inputStream)
                val stringBuilderList=listOf(StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"),StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"),StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"),StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"),StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"),StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"),StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"),StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"),StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"),StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"),StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"),StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"),StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"),StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n"))
                for(k in 0 until workBook.numberOfSheets){
                    val sheet = workBook.getSheet(k)
                    val fieldNameList=fieldNameLists[k]
                    for (i in 0 until sheet.columns-1) {
                        for(j in 1 until sheet.rows){
                            val text = sheet.getCell(i, j).contents.trim()
                            if(k==0&&(j<=4||j==11)){
                                if(text.isNotEmpty()) stringBuilderList[i].append("<string name=\"").append(if(j==11) fieldNameList[4] else fieldNameList[j-1]).append("\">").append(text).append("</string>").append("\n")
                            }else if(k==1){
                                if(text.isNotEmpty()){
                                    when (j) {
                                        32 -> stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-1]).append("\">").append(text).append("\n")
                                        33 -> stringBuilderList[i].append(text).append("</string>").append("\n")
                                        36 -> stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-1]).append("\">").append(text).append("\n")
                                        37 -> stringBuilderList[i].append(text).append("</string>").append("\n")
                                        39 -> stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-1]).append("\">").append(text).append("\n")
                                        in 40..42 -> stringBuilderList[i].append(text).append("\n")
                                        43 -> stringBuilderList[i].append(text).append("\n\n")
                                        44 -> stringBuilderList[i].append(text).append("\n")
                                        45 -> stringBuilderList[i].append(text).append("</string>").append("\n")
                                        else -> stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-1]).append("\">").append(text).append("</string>").append("\n")
                                    }
                                }
                            }else if(k==2){
                                if(text.isNotEmpty()){
                                    when (j) {
                                        in 1..7 -> stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-1]).append("\">").append(text).append("</string>").append("\n")
                                        9 -> stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-1]).append("\">").append(text).append("</string>").append("\n")
                                        in 11..14 -> stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-1]).append("\">").append(text).append("</string>").append("\n")
                                    }
                                }
                            }else if(k==3){
                                if(text.isNotEmpty()){
                                    when (j) {
                                        in 1..7 -> stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-1]).append("\">").append(text).append("</string>").append("\n")
                                        in 10..12 -> stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-3]).append("\">").append(text).append("</string>").append("\n")
                                        in 15..23 -> stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-5]).append("\">").append(text).append("</string>").append("\n")
                                        25 -> stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-6]).append("\">").append(text).append("</string>").append("\n")
                                        27 -> stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-7]).append("\">").append(text).append("</string>").append("\n")
                                        in 29..30 -> stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-8]).append("\">").append(text).append("</string>").append("\n")
                                        in 33..34 -> stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-10]).append("\">").append(text).append("</string>").append("\n")
                                    }
                                }
                            }else if(k==4){
                                if(j in 14..19)if(text.isNotEmpty())stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-14]).append("\">").append(text).append("</string>").append("\n")
                            }else if(k==5){
                                if(j in 2..6) if(text.isNotEmpty())stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-2]).append("\">").append(text).append("</string>").append("\n")
                            }else if(k==6){
                                when (j) {
                                    in 2..6 -> stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-2]).append("\">").append(text).append("</string>").append("\n")
                                    in 9..10 -> stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-4]).append("\">").append(text).append("</string>").append("\n")
                                    15->stringBuilderList[i].append("<string name=\"").append(fieldNameList[j-8]).append("\">").append(text).append("</string>").append("\n")
                                }
                            }
                        }
                    }
                }
                workBook.close()
//                System.out.println(stringBuilderList[0].toString())
                for(l in 0 until  stringBuilderList.size){
                    val fileContent=stringBuilderList[l].append("</resources>").toString()
                    writeStringToFile("/Users/dongcaizheng/Desktop/values/values-"+languageList[l],"/strings.xml",fileContent)
                }
            }
        }


        private fun writeStringToFile(directoryPath:String, filename: String, contentString: String) {
            try {
                val directory=File(directoryPath)
                if(!directory.exists()){
                    directory.mkdirs()
                }
                val fos = FileOutputStream(File(directory,filename))
                fos.write(contentString.toByteArray())
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

}