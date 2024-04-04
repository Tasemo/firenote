package de.oelkers.firenote.controllers.folder.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import de.oelkers.firenote.R
import de.oelkers.firenote.controllers.overview.FolderOverviewViewModel
import de.oelkers.firenote.models.Folder
import de.oelkers.firenote.models.NO_ICON_ID
import de.oelkers.firenote.util.GlobalAppState

private const val ARG_FOLDER = "ARG_FOLDER"
private const val NO_FOLDER_POSITION = -1

class EditFolderFragment : DialogFragment(), IconDialog.Callback {

    private val viewModel: FolderOverviewViewModel by activityViewModels()
    override val iconDialogIconPack: IconPack
        get() = (requireActivity().application as GlobalAppState).iconPack
    private var selectedIconId: Int = NO_ICON_ID

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_folder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        val folderIndex = requireArguments().getInt(ARG_FOLDER)
        val folder = if (folderIndex == NO_FOLDER_POSITION) {
            Folder()
        } else {
            viewModel.allFolders.value!![folderIndex]
        }
        val name = view.findViewById<EditText>(R.id.folder_name)
        val editIconButton = view.findViewById<MaterialButton>(R.id.editIconButton)
        val deleteButton = view.findViewById<MaterialButton>(R.id.deleteButton)
        val saveButton = view.findViewById<MaterialButton>(R.id.saveButton)
        name.setText(folder.name)
        editIconButton.setOnClickListener {
            val settings = IconDialogSettings.Builder()
            settings.maxSelection = 1
            IconDialog.newInstance(settings.build()).show(childFragmentManager, "iconDialog")
        }
        deleteButton.setOnClickListener {
            if (folderIndex != NO_FOLDER_POSITION) {
                viewModel.deleteFolder(folderIndex)
            }
            dismiss()
        }
        saveButton.setOnClickListener {
            folder.name = name.text.toString()
            if (selectedIconId != NO_ICON_ID) {
                folder.iconId = selectedIconId
            }
            if (folderIndex == NO_FOLDER_POSITION) {
                viewModel.addFolder(folder)
            } else {
                viewModel.updateFolder(folder, folderIndex)
            }
            dismiss()
        }
    }

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        assert(icons.size == 1)
        selectedIconId = icons[0].id
    }

    companion object {

        @JvmStatic
        fun newInstance(folder: Int? = null) = EditFolderFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_FOLDER, folder ?: NO_FOLDER_POSITION)
            }
        }
    }
}
