// HomeScreen.kt
package com.example.app_ricette

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

// Modern color palette with Italian inspiration - More blue/azure oriented
val PrimaryRed = Color(0xFF1E88E5)       // Changed to Azure Blue
val PrimaryRedVariant = Color(0xFF1565C0) // Changed to Darker Azure
val PrimaryGreen = Color(0xFF2E7D32)      // Basil Green (unchanged)
val PrimaryGreenLight = Color(0xFF66BB6A) // Light basil (unchanged)
val SecondaryBlue = Color(0xFF0D47A1)     // Deeper Mediterranean blue
val SecondaryBlueLight = Color(0xFF64B5F6) // Brighter light blue
val Background = Color(0xFFF5F9FF)        // Light azure background
val Surface = Color(0xFFFFFFFF)           // Pure white (unchanged)
val TextPrimary = Color(0xFF212121)       // Almost black (unchanged)
val TextSecondary = Color(0xFF757575)     // Medium gray (unchanged)

// Gradient for feature items
val ItalianFlagGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF009246).copy(alpha = 0.7f),  // Green (unchanged)
        Color(0xFFFFFFFF).copy(alpha = 0.7f),  // White (unchanged)
        Color(0xFF1E88E5).copy(alpha = 0.7f)   // Changed to Azure Blue
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val coroutineScope = rememberCoroutineScope()
    val apiService = RetrofitInstance.api
    var italianMeals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var randomMeal by remember { mutableStateOf<Meal?>(null) }
    var selectedMeal by remember { mutableStateOf<Meal?>(null) }
    val context = LocalContext.current

    // Load data when composable is first created
    LaunchedEffect(Unit) {
        try {
            // Load Italian meals
            val italianResponse = apiService.getItalianMeals()
            italianMeals = italianResponse.meals ?: emptyList()

            // Seleziona una ricetta casuale tra quelle italiane come "Ricetta del giorno"
            if (italianMeals.isNotEmpty()) {
                val randomIndex = (0 until italianMeals.size).random()
                val selectedItalianMeal = italianMeals[randomIndex]

                // Carica i dettagli completi della ricetta italiana
                val detailsResponse = apiService.getMealDetails(selectedItalianMeal.idMeal)
                randomMeal = detailsResponse.meals?.firstOrNull()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error loading data: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shadowElevation = 4.dp,
                color = Surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo più piccolo e moderno
                    Image(
                        painter = painterResource(id = R.mipmap.logo1_foreground),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

//                    Text(
//                        "DARIO'S COOKBOOK",
//                        style = TextStyle(
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 18.sp,
//                            color = PrimaryRed
//                        )
//                    )

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        },
        containerColor = Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Feature recipe section
            item {
                randomMeal?.let { meal ->
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "TODAY'S SPECIAL",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clickable {
                                    selectedMeal = meal
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 0.dp
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Recipe image
                                AsyncImage(
                                    model = meal.strMealThumb,
                                    contentDescription = meal.strMeal,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(16.dp))
                                )

                                // Gradient overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.7f)
                                                )
                                            )
                                        )
                                )

                                // Recipe name - favorite button removed
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomStart)
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        "Dario consiglia",
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    )

                                    Text(
                                        meal.strMeal,
                                        style = TextStyle(
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Category header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "CuciDario",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
            }

            // Recipe list
            if (italianMeals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryRed)
                    }
                }
            } else {
                items(italianMeals) { meal ->
                    ModernMealItem(meal = meal) {
                        coroutineScope.launch {
                            try {
                                val detailsResponse = apiService.getMealDetails(meal.idMeal)
                                selectedMeal = detailsResponse.meals?.firstOrNull()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error loading details: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
    }

    // Display meal details dialog when a meal is selected
    selectedMeal?.let { meal ->
        ModernMealDetailsDialog(meal = meal) {
            selectedMeal = null
        }
    }
}

@Composable
fun ModernMealItem(meal: Meal, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Recipe thumbnail
            AsyncImage(
                model = meal.strMealThumb,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            // Recipe details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = meal.strMeal,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Italian cuisine",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                )
            }

            // Favorite icon removed
        }
    }
}

@Composable
fun ModernMealDetailsDialog(meal: Meal, onDismiss: () -> Unit) {
    var showStepByStep by remember { mutableStateOf(false) }

    if (showStepByStep) {
        ModernStepByStepInstructions(meal = meal, onBack = { showStepByStep = false })
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(16.dp)),
            containerColor = Surface,
            title = null,
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    // Recipe image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        AsyncImage(
                            model = meal.strMealThumb,
                            contentDescription = meal.strMeal,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        )

                        // Back button
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(36.dp)
                                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }

                        // Favorite button removed
                    }

                    // Recipe details container
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Recipe title
                        Text(
                            text = meal.strMeal,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Recipe meta info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Chip(
                                text = meal.strCategory ?: "Main",
                                color = PrimaryGreenLight
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Chip(
                                text = meal.strArea ?: "Italian",
                                color = SecondaryBlueLight
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Ingredients section
                        SectionHeader("Ingredients")

                        Spacer(modifier = Modifier.height(8.dp))

                        // Ingredients list with modern styling
                        val ingredients = listOf(
                            Pair(meal.strIngredient1, meal.strMeasure1),
                            Pair(meal.strIngredient2, meal.strMeasure2),
                            Pair(meal.strIngredient3, meal.strMeasure3),
                            Pair(meal.strIngredient4, meal.strMeasure4),
                            Pair(meal.strIngredient5, meal.strMeasure5),
                            Pair(meal.strIngredient6, meal.strMeasure6),
                            Pair(meal.strIngredient7, meal.strMeasure7),
                            Pair(meal.strIngredient8, meal.strMeasure8),
                            Pair(meal.strIngredient9, meal.strMeasure9),
                            Pair(meal.strIngredient10, meal.strMeasure10),
                            Pair(meal.strIngredient11, meal.strMeasure11),
                            Pair(meal.strIngredient12, meal.strMeasure12),
                            Pair(meal.strIngredient13, meal.strMeasure13),
                            Pair(meal.strIngredient14, meal.strMeasure14),
                            Pair(meal.strIngredient15, meal.strMeasure15),
                            Pair(meal.strIngredient16, meal.strMeasure16),
                            Pair(meal.strIngredient17, meal.strMeasure17),
                            Pair(meal.strIngredient18, meal.strMeasure18),
                            Pair(meal.strIngredient19, meal.strMeasure19),
                            Pair(meal.strIngredient20, meal.strMeasure20)
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Background
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                ingredients.forEach { (ingredient, measure) ->
                                    if (!ingredient.isNullOrBlank() && !measure.isNullOrBlank()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(PrimaryRed, CircleShape)
                                            )

                                            Spacer(modifier = Modifier.width(8.dp))

                                            Text(
                                                text = "$measure",
                                                style = TextStyle(
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = TextPrimary
                                                ),
                                                modifier = Modifier.width(80.dp)
                                            )

                                            Text(
                                                text = ingredient,
                                                style = TextStyle(
                                                    fontSize = 14.sp,
                                                    color = TextSecondary
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Start cooking button
                    Button(
                        onClick = { showStepByStep = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryRed
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            "START COOKING",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    )
}

@Composable
fun Chip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernStepByStepInstructions(meal: Meal, onBack: () -> Unit) {
    // Estrarre i passi dal testo delle istruzioni
    val instructionSteps = extractInstructionSteps(meal.strInstructions)

    // Stato per tenere traccia del passo corrente
    var currentStep by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Step by Step",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { (currentStep + 1).toFloat() / instructionSteps.size.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = PrimaryGreen,
                trackColor = PrimaryGreenLight.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Step counter
            Text(
                text = "STEP ${currentStep + 1} OF ${instructionSteps.size}",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextSecondary
                )
            )

            // Visualizza il passo corrente
            if (instructionSteps.isNotEmpty() && currentStep < instructionSteps.size) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        Column {
                            // Current step instruction
                            Text(
                                text = instructionSteps[currentStep],
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    lineHeight = 28.sp,
                                    color = TextPrimary
                                )
                            )

                            // Immagine del cibo (opzionale, mostrata solo per il primo passo)
                            if (currentStep == 0) {
                                Spacer(modifier = Modifier.height(24.dp))
                                AsyncImage(
                                    model = meal.strMealThumb,
                                    contentDescription = meal.strMeal,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }
                        }
                    }
                }

                // Navigation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Previous button
                    OutlinedButton(
                        onClick = { if (currentStep > 0) currentStep-- },
                        enabled = currentStep > 0,
                        modifier = Modifier.width(140.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (currentStep > 0) SecondaryBlue else Color.Gray
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (currentStep > 0) SecondaryBlue else Color.Gray
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("Previous")
                    }

                    // Next/Finish button
                    Button(
                        onClick = {
                            if (currentStep < instructionSteps.size - 1) {
                                currentStep++
                            } else {
                                // Ultimo passo, torna alla scheda info
                                onBack()
                            }
                        },
                        modifier = Modifier.width(140.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentStep == instructionSteps.size - 1)
                                PrimaryRed else PrimaryGreen
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            if (currentStep == instructionSteps.size - 1)
                                "Finish" else "Next"
                        )
                    }
                }
            } else {
                // Fallback nel caso non ci siano istruzioni
                Text("No instructions available for this recipe.")

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryRed
                    )
                ) {
                    Text("Go Back")
                }
            }
        }
    }
}

// Funzione per estrarre i passi dalle istruzioni
fun extractInstructionSteps(instructions: String): List<String> {
    // Dividi le istruzioni in base ai punti, punti e virgola o numeri seguiti da punto
    val steps = instructions.split(Regex("\\. |\\.$|\\; |\\d+\\."))
        .filter { it.isNotBlank() }
        .map { it.trim() }

    // Se non ci sono passi chiari, torna l'intera istruzione come unico passo
    return if (steps.isEmpty()) listOf(instructions) else steps
}