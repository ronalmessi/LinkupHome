package com.ihomey.linkuphome

import java.io.File
import java.io.FileInputStream
import jxl.Workbook
import java.io.FileOutputStream


class StringResourceGenerator {

    companion object {

        private val fieldNameLists= listOf(
                listOf("splash_tip_agreement","splash_user_license_agreement","splash_corporate_privacy_statement","splash_action_start","error_network"),
                listOf("title_create_zone","action_save","msg_default_zone","tip_name_zone","home","device","msg_no_device","msg_add_device","zone","setting","action_delete","action_remove_device","action_cancel","action_confirm","title_choose_device_type","title_lamp_m1","title_lamp_n1","title_lamp_a2","title_lamp_r2","title_lamp_c3","title_lamp_v1","title_lamp_s2","title_lamp_s1","title_lamp_t1","msg_devie_","home","home","home","home","home","home","home"),
                listOf("splash_tip_agreement","splash_user_license_agreement","splash_corporate_privacy_statement","splash_action_start","error_network"),
                listOf("splash_tip_agreement","splash_user_license_agreement","splash_corporate_privacy_statement","splash_action_start","error_network"),
                listOf("splash_tip_agreement","splash_user_license_agreement","splash_corporate_privacy_statement","splash_action_start","error_network"),
                listOf("splash_tip_agreement","splash_user_license_agreement","splash_corporate_privacy_statement","splash_action_start","error_network"),
                listOf("splash_tip_agreement","splash_user_license_agreement","splash_corporate_privacy_statement","splash_action_start","error_network")
        )

        private val languageList= listOf("zh-rCN","en","fr","de","es","nl","zh-rTW","pt","it","ja","ru","da","sv","pl")


        @JvmStatic
        fun main(args: Array<String>) {
            val file = File("/Users/dongcaizheng/Desktop/language.xls")
            if(file.isFile&&file.exists()){
                val inputStream =FileInputStream(file)
                val workBook = Workbook.getWorkbook(inputStream)
                val stringBuilderList=listOf(StringBuilder(),StringBuilder(),StringBuilder(),StringBuilder(),StringBuilder(),StringBuilder(),StringBuilder(),StringBuilder(),StringBuilder(),StringBuilder(),StringBuilder(),StringBuilder(),StringBuilder(),StringBuilder())
                for(k in 0 until workBook.numberOfSheets){
                    val sheet = workBook.getSheet(k)
                    val fieldNameList=fieldNameLists[k]
                    for (i in 0 until sheet.columns-1) {
                        for(j in 1 until sheet.rows){
                            val text = sheet.getCell(i, j).contents.trim()
                            if(k==0&&(j<=4||j==11)){
                                if(text.isNotEmpty()) stringBuilderList[i].append("<string name=\"").append(if(j==11) fieldNameList[4] else fieldNameList[j-1]).append("\">").append(text).append("</string>").append("\n")
                            }else if(k>0){
                                if(text.isNotEmpty()) stringBuilderList[i].append("<string name=\"app_name\">").append(text).append("</string>").append("\n")
                            }
                        }
                    }
                }
                workBook.close()
//                System.out.println(stringBuilderList[0].toString())
                for(l in 0 until  stringBuilderList.size){
                    val fileContent=stringBuilderList[l].toString()
                    writeStringToFile("/Users/dongcaizheng/Desktop/values/values-"+languageList[l],"/string.xml",fileContent)
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