package fi.anttihemminki.holygrain

import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject

val server_str = "http://192.168.100.110/hg_backend/" // http://www.anttihemminki.fi/HolyGrainServer/

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
    json.put("model", android.os.Build.MODEL)
    json.put("app_time", System.currentTimeMillis())

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

/*fun saveTestset(name: String, testSetName: String, testSetIndex: Int, faceData: FaceData, okFunc: (JSONObject) -> Unit, errorFunc: () -> Unit) {
    val queue = Volley.newRequestQueue(ACTIVE_ACTIVITY)
    val url = "${server_str}save_distance_data.php"

    val json = JSONObject()
    json.put("test_person_name", name)
    json.put("test_set_name", testSetName)
    json.put("test_set_index", testSetIndex)
    json.put("model", android.os.Build.MODEL)
    json.put("app_time", System.currentTimeMillis())
    json.put("data", Gson().toJson(faceData))

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
}*/

fun sendDistanceData(data: DistanceData,
                     okFunc: (JSONObject) -> Unit, errorFunc: () -> Unit) {
    val queue = Volley.newRequestQueue(ACTIVE_ACTIVITY)
    val url = "${server_str}save_distance_data.php"



    val json = JSONObject()
    json.put("id", data.ID)

    //json.put("covertype", data.testState.cover)
    json.put("distance_target", data.testState.distance)
    json.put("eye", data.testState.eye)

    json.put("test_set_index", data.index)

    json.put("rotx", data.rotx)
    json.put("roty", data.roty)
    json.put("rotz", data.rotz)

    json.put("measure_time", data.time)
    json.put("distance_measured", data.distance)

    json.put("right_eye_open_prob", data.rightEyeOpenProb)
    json.put("left_eye_open_prob", data.leftEyeOpenProb)

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