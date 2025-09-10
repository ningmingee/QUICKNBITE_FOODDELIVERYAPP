package com.example.quicknbiteapp.ui.vendor

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.quicknbiteapp.R
import com.example.quicknbiteapp.ui.customer.HtmlWebView
import com.example.quicknbiteapp.utils.HtmlLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorAgreementScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val htmlContent = HtmlLoader.loadHtmlFromAssets(context, "vendor-agreement.html")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.view_vendor_agreement),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        HtmlWebView(
            htmlContent = htmlContent,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}