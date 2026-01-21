package com.noor.khabarlagbe.presentation.schedule

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.domain.model.*
import com.noor.khabarlagbe.ui.theme.*
import com.noor.khabarlagbe.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(
    navController: NavController,
    viewModel: ScheduleOrderViewModel = hiltViewModel()
) {
    val mealPlans by viewModel.mealPlans.collectAsState()
    var selectedDay by remember { mutableIntStateOf(2) } // Monday

    val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Plans", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Create new plan */ }) {
                        Icon(Icons.Filled.Add, "New Plan")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Week overview
            item {
                WeekOverviewCard(
                    days = days,
                    selectedDay = selectedDay,
                    onDaySelect = { selectedDay = it },
                    mealPlans = mealPlans
                )
            }

            // Selected day meals
            item {
                Text(
                    text = "${days[selectedDay]}'s Meals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            val dayMeals = mealPlans.flatMap { plan ->
                plan.meals.filter { it.dayOfWeek == selectedDay + 1 }
            }

            if (dayMeals.isEmpty()) {
                item {
                    EmptyDayCard(
                        day = days[selectedDay],
                        onAddMeal = { /* Add meal */ }
                    )
                }
            } else {
                items(dayMeals) { meal ->
                    PlannedMealCard(
                        meal = meal,
                        onEdit = { /* Edit meal */ },
                        onRemove = { /* Remove meal */ }
                    )
                }
            }

            // Active plans
            if (mealPlans.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your Meal Plans",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(mealPlans) { plan ->
                    MealPlanCard(
                        plan = plan,
                        onToggle = { viewModel.toggleMealPlan(plan.id) },
                        onEdit = { /* Edit plan */ }
                    )
                }
            }

            // Suggestions
            item {
                MealSuggestionsCard()
            }
        }
    }
}

@Composable
private fun WeekOverviewCard(
    days: List<String>,
    selectedDay: Int,
    onDaySelect: (Int) -> Unit,
    mealPlans: List<MealPlan>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "This Week",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEachIndexed { index, day ->
                    val hasPlannedMeal = mealPlans.any { plan ->
                        plan.meals.any { it.dayOfWeek == index + 1 }
                    }
                    
                    DayCell(
                        day = day,
                        isSelected = selectedDay == index,
                        hasPlannedMeal = hasPlannedMeal,
                        onClick = { onDaySelect(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: String,
    isSelected: Boolean,
    hasPlannedMeal: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Primary else TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = when {
                        isSelected -> Primary
                        hasPlannedMeal -> Primary.copy(alpha = 0.2f)
                        else -> SurfaceVariant
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (hasPlannedMeal) {
                Icon(
                    Icons.Filled.Restaurant,
                    null,
                    tint = if (isSelected) Color.White else Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun PlannedMealCard(
    meal: PlannedMeal,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    val mealTypeIcon = when (meal.mealType) {
        MealType.BREAKFAST -> Icons.Filled.FreeBreakfast
        MealType.LUNCH -> Icons.Filled.LunchDining
        MealType.DINNER -> Icons.Filled.DinnerDining
        MealType.SNACK -> Icons.Filled.Icecream
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(mealTypeIcon, null, tint = Primary)
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.mealType.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelMedium,
                    color = Primary
                )
                Text(
                    text = meal.restaurantName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${Constants.CURRENCY_SYMBOL}${meal.estimatedCost.toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, null, tint = TextSecondary)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Close, null, tint = Error)
            }
        }
    }
}

@Composable
private fun EmptyDayCard(day: String, onAddMeal: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.RestaurantMenu,
                null,
                tint = TextDisabled,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No meals planned for $day",
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onAddMeal) {
                Icon(Icons.Filled.Add, null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Meal")
            }
        }
    }
}

@Composable
private fun MealPlanCard(
    plan: MealPlan,
    onToggle: () -> Unit,
    onEdit: () -> Unit
) {
    val totalCost = plan.meals.sumOf { it.estimatedCost }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plan.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${plan.meals.size} meals • ~${Constants.CURRENCY_SYMBOL}${totalCost.toInt()}/week",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                
                Switch(
                    checked = plan.isActive,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(checkedTrackColor = Primary)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(plan.meals.take(5)) { meal ->
                    Surface(
                        color = Primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = meal.restaurantName,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MealSuggestionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Info.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.AutoAwesome,
                null,
                tint = Info,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Smart Suggestions",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Get personalized meal recommendations based on your preferences",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            IconButton(onClick = { /* Get suggestions */ }) {
                Icon(Icons.Filled.ChevronRight, null, tint = Info)
            }
        }
    }
}
