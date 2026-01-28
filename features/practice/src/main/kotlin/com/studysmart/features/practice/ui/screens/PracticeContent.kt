package com.studysmart.features.practice.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studysmart.features.practice.domain.PracticeSession
import com.studysmart.features.practice.domain.SessionQuestion

@Composable
fun PracticeStartContent(onStart: (Long) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sesión de Práctica", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Prepárate para repasar tus conocimientos. Las preguntas falladas volverán a aparecer.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { onStart(1L) }, // TODO: Pass real quiz ID
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Empezar")
        }
    }
}

@Composable
fun PracticeQuestionContent(
    question: SessionQuestion,
    progress: Int,
    total: Int,
    onOptionSelected: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LinearProgressIndicator(
            progress = { if (total > 0) progress.toFloat() / total else 0f },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Progreso: $progress preguntas", style = MaterialTheme.typography.labelSmall)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = question.text,
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(question.options) { option ->
                OutlinedButton(
                    onClick = { onOptionSelected(option.id) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(option.text, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Composable
fun PracticeFeedbackContent(
    isCorrect: Boolean,
    correctOptionId: Long,
    explanation: String?,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val titleColor = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
        val title = if (isCorrect) "¡Correcto!" else "Incorrecto"
        
        Icon(
            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = titleColor,
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(title, color = titleColor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        if (!isCorrect) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("La respuesta correcta era...", style = MaterialTheme.typography.bodySmall)
        }
        
        explanation?.let {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Explicación", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar")
        }
    }
}

@Composable
fun PracticeSummaryContent(
    session: PracticeSession,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("¡Sesión Completada!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            SummaryStat(label = "Respondidas", value = "${session.stats.totalQuestionsAnswered}")
            SummaryStat(label = "Correctas", value = "${session.stats.correctAnswers}")
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
            Text("Volver al Inicio")
        }
    }
}

@Composable
private fun SummaryStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}
