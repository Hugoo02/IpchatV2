package ipca.project.ipchatv2.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.databinding.FragmentGroupListBinding
import ipca.project.ipchatv2.databinding.FragmentUserListBinding

class GroupListFragment : Fragment() {
    private lateinit var binding: FragmentGroupListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupListBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment


        return binding.root
    }

}