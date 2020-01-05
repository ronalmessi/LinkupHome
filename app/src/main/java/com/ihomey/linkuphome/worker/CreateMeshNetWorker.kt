package com.ihomey.linkuphome.worker


import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.pairlink.sigmesh.lib.MeshNetInfo
import com.pairlink.sigmesh.lib.PlSigMeshService
import com.pairlink.sigmesh.lib.Util
import java.util.*


class CreateMeshNetWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val meshNetId = (10000000 + Random(System.currentTimeMillis()).nextInt(20000000)).toString()
        val netKey = ByteArray(16)
        val appKey = ByteArray(16)
        for (i in 0..15) {
            netKey[i] = 0
            appKey[i] = 0
        }
        System.arraycopy(meshNetId.toByteArray(), 0, netKey, 0, 4)
        System.arraycopy(meshNetId.toByteArray(), 4, appKey, 0, 4)
        val mesh_net = MeshNetInfo()
        mesh_net.name = meshNetId
        mesh_net.admin_next_addr = 0x7ffe
        mesh_net.mesh_version = 0
        mesh_net.gateway = false
        mesh_net.netkey = Util.byte2HexStr(netKey)
        mesh_net.appkey = Util.byte2HexStr(appKey)
        mesh_net.iv_index = 0
        mesh_net.seq = 1
        mesh_net.nodes.clear()
        mesh_net.admin_nodes.clear()

        val admin_node = MeshNetInfo.AdminNodeInfo()
        admin_node.uuid = PlSigMeshService.getInstance().current_admin
        admin_node.name = "user"
        admin_node.addr = 0x7fff
        admin_node.provision_start_addr = Util.PROVISION_DEFAULT_START_ADDR
        admin_node.provision_end_addr = admin_node.provision_start_addr + Util.PROVISION_NUM_LIMIT_WITHOUT_QUERY_SERVER
        admin_node.node_next_addr = admin_node.provision_start_addr.toShort()
        mesh_net.admin_nodes.add(admin_node)
        PlSigMeshService.getInstance().addMeshNet(mesh_net)
        val resultData = Data.Builder().putString("result", Gson().toJson(mesh_net)).build()
        return Result.success(resultData)
    }
}