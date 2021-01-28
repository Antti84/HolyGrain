package fi.anttihemminki.holygrain

import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

fun testServer(okFunc: () -> Unit, errorFunc: () -> Unit) {
    val queue = Volley.newRequestQueue(ACTIVE_ACTIVITY)
    val url = "http://www.anttihemminki.fi/HolyGrainServer/receive_all_face_points.php"

    val jsonRequest = JsonObjectRequest(
        Request.Method.POST,
        url,
        null,
        { okFunc() },
        { errorFunc() }
    )
    queue.add(jsonRequest)
}

fun sendFaceDataToServer(data: JSONObject, okFunc: () -> Unit, errorFunc: () -> Unit) {
    val queue = Volley.newRequestQueue(ACTIVE_ACTIVITY)
    val url = "http://www.anttihemminki.fi/HolyGrainServer/receive_all_face_points.php"

    val jsonRequest = JsonObjectRequest(
        Request.Method.POST,
        url,
        data,
        { okFunc() },
        { errorFunc() }
    )
    queue.add(jsonRequest)
}