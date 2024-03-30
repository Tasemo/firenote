package de.oelkers.firenote.controllers.overview

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import de.oelkers.firenote.controllers.folder.FolderFragment

class FolderAdapter(activity: FragmentActivity, private val viewModel: FolderOverviewViewModel) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = viewModel.allFolders.value!!.size

    override fun createFragment(position: Int): Fragment {
        return FolderFragment.newInstance(position)
    }
}
