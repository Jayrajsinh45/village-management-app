package com.villagemanagement.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.villagemanagement.BuildConfig
import com.villagemanagement.data.model.Village
import com.villagemanagement.ui.viewmodel.VillageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVillageScreen(
    onNavigateBack: () -> Unit,
    villageViewModel: VillageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val villageState by villageViewModel.villageState.collectAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var population by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var adminName by remember { mutableStateOf("") }
    var adminContact by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }

    // Initialize Places API
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context, BuildConfig.MAPS_API_KEY)
        }
    }

    // Places Autocomplete Launcher
    val autocompleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val place = result.data?.let { Autocomplete.getPlaceFromIntent(it) }
            place?.let {
                // Auto-fill location data
                it.name?.let { placeName -> name = placeName }
                it.latLng?.let { latLng ->
                    latitude = latLng.latitude.toString()
                    longitude = latLng.longitude.toString()
                }
                it.address?.let { addr -> address = addr }
                
                Toast.makeText(context, "Location selected: ${it.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openPlacesPicker() {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(context)
        autocompleteLauncher.launch(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Village") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Search Location Button
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "📍 Search Location",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { openPlacesPicker() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Search Village on Google Maps")
                        }
                        if (address.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Selected: $address",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Divider()
                Text(
                    text = "Or enter details manually:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Village Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = population,
                        onValueChange = { if (it.all { char -> char.isDigit() }) population = it },
                        label = { Text("Population") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = area,
                        onValueChange = { area = it },
                        label = { Text("Area (sq km)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = adminName,
                    onValueChange = { adminName = it },
                    label = { Text("Village Admin Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = adminContact,
                    onValueChange = { if (it.all { char -> char.isDigit() }) adminContact = it },
                    label = { Text("Admin Contact Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = { Text("Latitude") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = { Text("Longitude") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (name.isBlank() || population.isBlank()) {
                            Toast.makeText(context, "Name and Population are required", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (latitude.isBlank() || longitude.isBlank()) {
                            Toast.makeText(context, "Please select a location or enter coordinates", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isSubmitting = true
                        
                        val lat = latitude.toDoubleOrNull() ?: 0.0
                        val lng = longitude.toDoubleOrNull() ?: 0.0
                        
                        val villageLocation = com.villagemanagement.data.model.VillageLocation(
                            latitude = lat,
                            longitude = lng,
                            address = address
                        )

                        val village = Village(
                            name = name,
                            description = description,
                            population = population.toIntOrNull() ?: 0,
                            area = area,
                            adminName = adminName,
                            adminContact = adminContact,
                            location = villageLocation
                        )

                        villageViewModel.createVillage(village) { success, message ->
                            isSubmitting = false
                            if (success) {
                                Toast.makeText(context, "Village added successfully!", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            } else {
                                Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Add Village")
                    }
                }
            }
        }
    }
}
