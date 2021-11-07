package ipca.project.ipchatv2


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import ipca.project.ipchatv2.databinding.ActivityMainBinding
import ipca.project.ipchatv2.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.connectionLiveData(requireContext())

        fetchUsers()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun fetchUsers(){

        db.collection("User")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    println(document.data)
                }
            }
            .addOnFailureListener { exception ->
                println("Erro")
            }

    }

}