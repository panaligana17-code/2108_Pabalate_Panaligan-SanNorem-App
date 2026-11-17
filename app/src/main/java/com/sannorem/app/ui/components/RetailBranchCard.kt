package com.sannorem.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sannorem.app.domain.model.Brand
import com.sannorem.app.domain.model.RetailBranch

@Composable
fun RetailBranchCard(
    branch: RetailBranch,
    onViewOnMap: (RetailBranch) -> Unit,
    modifier: Modifier = Modifier
) {
    val brandBadgeBg = when (branch.brand) {
        Brand.SEVEN_ELEVEN -> Color(0xFFFFF7ED) // bg-orange-50
        Brand.ALFAMART -> Color(0xFFFEF2F2) // bg-red-50
        Brand.DALI -> Color(0xFFEFF6FF) // bg-blue-50
    }
    val brandBadgeBorder = when (branch.brand) {
        Brand.SEVEN_ELEVEN -> Color(0xFFFED7AA) // border-orange-200
        Brand.ALFAMART -> Color(0xFFFECACA) // border-red-200
        Brand.DALI -> Color(0xFFBFDBFE) // border-blue-200
    }
    val brandTextColor = when (branch.brand) {
        Brand.SEVEN_ELEVEN -> Color(0xFFEA580C) // text-orange-600
        Brand.ALFAMART -> Color(0xFFDC2626) // text-red-600
        Brand.DALI -> Color(0xFF2563EB) // text-blue-600
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: Brand badge & Status dot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = brandBadgeBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, brandBadgeBorder)
                    ) {
                        Text(
                            text = when (branch.brand) {
                                Brand.SEVEN_ELEVEN -> "7-Eleven"
                                Brand.ALFAMART -> "Alfamart"
                                Brand.DALI -> "Dali"
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = brandTextColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = branch.branchName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        lineHeight = 22.sp
                    )

                    Text(
                        text = "ID: ${branch.id}",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (branch.verified) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFECFDF5),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                        ) {
                            Text(
                                text = "Verified store · approx. position",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF047857)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Text(
                                text = "Active Branch",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Brand Color Dot Icon Container
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(brandTextColor)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Details List
            BranchDetailRow(label = "Hours:", value = branch.hours)
            BranchDetailRow(label = "Barangay:", value = branch.barangayName.ifBlank { "Nasugbu" })
            BranchDetailRow(label = "Base Radius:", value = "${branch.serviceRadiusKm} km")
            BranchDetailRow(
                label = "Coordinates:",
                value = "${"%.4f".format(branch.latitude)}, ${"%.4f".format(branch.longitude)}",
                isMonospace = true
            )

            // Brand Specific Attributes
            if (branch.attributes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "BRAND SPECIFIC ATTRIBUTES",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                branch.attributes.forEach { attr ->
                    Text(
                        text = attr.uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = brandTextColor
                    )
                }
            }

            // Coverage Strategy box
            if (branch.strategy.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "COVERAGE STRATEGY:",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = branch.strategy.uppercase(),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // "View on map →" action button
            Text(
                text = "View on map →",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF047857), // emerald-700
                modifier = Modifier
                    .clickable { onViewOnMap(branch) }
                    .padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun BranchDetailRow(
    label: String,
    value: String,
    isMonospace: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF64748B) // text-slate-500
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
            color = Color(0xFF1E293B) // text-slate-800
        )
    }
}
