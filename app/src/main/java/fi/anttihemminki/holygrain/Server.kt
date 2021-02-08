package fi.anttihemminki.holygrain

import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

val server_str = "http://192.168.100.101/hg_backend/" // http://www.anttihemminki.fi/HolyGrainServer/

fun testServer(okFunc: () -> Unit, errorFunc: () -> Unit) {
    val queue = Volley.newRequestQueue(ACTIVE_ACTIVITY)
    val url = "${server_str}test_holygrain_server.php"

    val jsonRequest = JsonObjectRequest(
        Request.Method.POST,
        url,
        null,
        { okFunc() },
        {
                error ->
            errorFunc()
            Log.e("TAG","response: ${error.message}")
        }
    )
    queue.add(jsonRequest)
}

fun validateTestsetName(name: String, okFunc: (JSONObject) -> Unit, errorFunc: () -> Unit) {
    val queue = Volley.newRequestQueue(ACTIVE_ACTIVITY)
    val url = "${server_str}validate_distance_testset_name.php"

    val json = JSONObject()
    json.put("testset_name", name)

    val jsonRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            json,
            { data ->
                okFunc(data)
            },
            {
                error ->
                errorFunc()
                Log.e("TAG","response: ${error.message}")
            }
    )

    queue.add(jsonRequest)
}

fun createTestset(name: String, okFunc: (JSONObject) -> Unit, errorFunc: () -> Unit) {
    val queue = Volley.newRequestQueue(ACTIVE_ACTIVITY)
    val url = "${server_str}create_distance_testset.php"

    val json = JSONObject()
    json.put("testset_name", name)

    val jsonRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            json,
            { data ->
                okFunc(data)
            },
            {
                error ->
                errorFunc()
                Log.e("TAG","response: ${error.message}")
            }
    )

    queue.add(jsonRequest)
}

fun sendFaceDataToServer(data: JSONObject, okFunc: () -> Unit, errorFunc: () -> Unit) {
    val queue = Volley.newRequestQueue(ACTIVE_ACTIVITY)
    val url = "${server_str}receive_all_face_points.php"

    val jsonRequest = JsonObjectRequest(
        Request.Method.POST,
        url,
        data,
        { okFunc() },
        {
                error ->
            errorFunc()
            Log.e("TAG","response: ${error.message}")
        }
    )
    queue.add(jsonRequest)
}