package de.oelkers.firenote.controllers.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.oelkers.firenote.controllers.folder.FolderViewModel
import de.oelkers.firenote.models.Folder
import de.oelkers.firenote.persistence.FolderRepository

class FolderOverviewViewModel(repository: FolderRepository) : ViewModel() {

    private val folderViewModels = HashMap<Int, FolderViewModel>()
    private val _filterValue = MutableLiveData<CharSequence?>()
    val filterValue: LiveData<CharSequence?>
        get() = _filterValue

    private val _allFolders = MutableLiveData<List<Folder>>(repository.readAllFolders(true))
    val allFolders: LiveData<List<Folder>>
        get() = _allFolders

    fun getViewModelFor(folder: Int): FolderViewModel {
        return folderViewModels.getOrPut(folder) {
            FolderViewModel(this, folder)
        }
    }

    fun setFilterValue(text: CharSequence?) {
        _filterValue.value = text
    }

    fun isAnySelected(): Boolean {
        return folderViewModels.values.any(FolderViewModel::isAnySelected)
    }

    fun deleteSelected() {
        folderViewModels.values.forEach(FolderViewModel::deleteSelected)
    }

    fun updateFolder(folder: Folder, index: Int) {
        perform { it[index] = folder }
    }

    private fun perform(task: (all: MutableList<Folder>) -> Unit) {
        val newList = allFolders.value!!.toMutableList()
        task(newList)
        _allFolders.postValue(newList)
    }
}

class NoteViewModelFactory(private val repository: FolderRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") return FolderOverviewViewModel(repository) as T
    }
}
