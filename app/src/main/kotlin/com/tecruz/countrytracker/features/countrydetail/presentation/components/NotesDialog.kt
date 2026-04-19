package com.tecruz.countrytracker.features.countrydetail.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tecruz.countrytracker.R
import com.tecruz.countrytracker.core.designsystem.OnSurfaceVariant
import com.tecruz.countrytracker.core.designsystem.PrimaryGreen
import com.tecruz.countrytracker.core.designsystem.Surface
import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail

@Composable
fun NotesDialog(currentNotes: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var tempNotes by remember { mutableStateOf(currentNotes) }
    val maxLength = CountryDetail.MAX_NOTES_LENGTH
    val isOverLimit = tempNotes.length > maxLength

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_notes)) },
        text = {
            Column {
                OutlinedTextField(
                    value = tempNotes,
                    onValueChange = { newValue ->
                        tempNotes = newValue
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp),
                    placeholder = { Text(stringResource(R.string.notes_placeholder)) },
                    minLines = 5,
                    maxLines = 10,
                    shape = RoundedCornerShape(12.dp),
                    isError = isOverLimit,
                    supportingText = {
                        Text(
                            text = stringResource(R.string.notes_character_count, tempNotes.length, maxLength),
                            color = if (isOverLimit) MaterialTheme.colorScheme.error else OnSurfaceVariant,
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isOverLimit) MaterialTheme.colorScheme.error else PrimaryGreen,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                    ),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(tempNotes)
                    onDismiss()
                },
                enabled = !isOverLimit,
            ) {
                Text(
                    stringResource(R.string.save),
                    color = if (isOverLimit) OnSurfaceVariant else PrimaryGreen,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = Surface,
    )
}
