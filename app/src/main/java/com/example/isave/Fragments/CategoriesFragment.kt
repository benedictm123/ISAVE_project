package com.example.isave.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.isave.R
import com.example.isave.databinding.FragmentCategoriesBinding


class CategoriesFragment : Fragment() {

    private lateinit var binding:FragmentCategoriesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCategoriesBinding.inflate(inflater,container,false)
        return binding.root
    }


}