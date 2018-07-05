object Versions {
    const val  supportLibrary_version = "26.1.0"
    const val  kotlin_version = "1.2.31"
    const val  dagger_version = "2.14.1"
    const val  arch_version = "1.0.0"
    const val  spongycastle_version = "1.51.0.0"
    const val  qiniu_version = "7.3.12"
    const val  gson_version = "2.7"
    const val  baseRecyclerViewAdapterHelperVersion = "2.9.31"
}

object Libs {
    
    val support_design="com.android.support:design:${Versions.supportLibrary_version}"
    val support_cardview="com.android.support:cardview-v7:${Versions.supportLibrary_version}"
    val support_constraint="com.android.support.constraint:constraint-layout:1.0.2"

    val liveData_runtime="android.arch.lifecycle:runtime:${Versions.arch_version}"
    val liveData_extensions="android.arch.lifecycle:extensions:${Versions.arch_version}"
    val liveData_compiler="android.arch.lifecycle:compiler:${Versions.arch_version}"
    val room_runtime="android.arch.persistence.room:runtime:${Versions.arch_version}"
    val room_compiler="android.arch.persistence.room:compiler:${Versions.arch_version}"

    val spongycastle_core="com.madgag.spongycastle:core:${Versions.spongycastle_version}"
    val spongycastle_pkix="com.madgag.spongycastle:pkix:${Versions.spongycastle_version}"

    val dagger="com.google.dagger:dagger-android:${Versions.dagger_version}"
    val dagger_compiler="com.google.dagger:dagger-compiler:${Versions.dagger_version}"

    val kotlin="org.jetbrains.kotlin:kotlin-stdlib-jre7:${Versions.kotlin_version}"

    val qiniu="com.qiniu:qiniu-android-sdk:${Versions.qiniu_version}"

    val gson="com.google.code.gson:gson:${Versions.gson_version}"

    val baseRecyclerViewAdapterHelper="com.github.CymChad:BaseRecyclerViewAdapterHelper:${Versions.baseRecyclerViewAdapterHelperVersion}"


}
