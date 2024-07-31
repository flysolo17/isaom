package com.ketchupzzz.isaom.presentation.auth.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.packInts
import androidx.navigation.NavHostController
import com.ketchupzzz.isaom.R
import com.ketchupzzz.isaom.presentation.routes.AppRouter
import com.ketchupzzz.isaom.ui.custom.PrimaryButton

@Composable
fun LoginScreen(modifier: Modifier = Modifier,
                navHostController: NavHostController,
                state : LoginState,
                events: (LoginEvents) -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(state) {
        if (state.isLoggedIn) {
            Toast.makeText(context,"Successfully Logged in",Toast.LENGTH_SHORT).show()
            navHostController.navigate(AppRouter.MainRoutes.route)
        }
        if (state.error !== null) {
            Toast.makeText(context,state.error,Toast.LENGTH_SHORT).show()
        }
    }
    Scaffold {
        Column(modifier = modifier
            .fillMaxSize()
            .padding(it)
            .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = modifier.weight(1f))
            Image(painter = painterResource(id = R.drawable.logo),modifier = modifier
                .width(200.dp)
                .height(200.dp), contentDescription = stringResource(R.string.logo))
            LoginForm(navHostController = navHostController, state = state, events = events) {

            }
            Spacer(modifier = modifier.weight(1f))
            TextButton(onClick = { navHostController.navigate(AppRouter.RegisterScreen.route) }) {
                Text(text = "No account yet ? register here")
            }
        }
    }
}

@Composable
fun LoginForm(modifier: Modifier = Modifier, navHostController: NavHostController, state : LoginState, events : (LoginEvents) -> Unit, onRegister : () -> Unit) {
    Box(modifier = modifier
        .wrapContentSize()
    ) {
        Column(modifier = modifier
            .padding(16.dp)
            .wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(state.email.value,
                onValueChange = {
                    events(LoginEvents.OnEmailChanged(it))
                },
                modifier = modifier.fillMaxWidth(),
                isError = state.email.isError,
                label = {
                    Text(text = "Email")
                },
                supportingText = {
                    Text(
                        text =  state.email.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Start
                    )
                },

                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = state.password.value,
                maxLines = 1,
                isError = state.password.isError,

                onValueChange = {
                    events(LoginEvents.OnPasswordChanged(it))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = {
                        events(LoginEvents.OnTogglePasswordVisibility)
                    }) {
                        Icon(
                            painter = painterResource(id = if (state.isPasswordVisible) R.drawable.ic_eye_off else R.drawable.ic_eye),
                            contentDescription = if (state.isPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                label = {
                    Text(text = stringResource(R.string.password))
                },
                modifier = modifier.fillMaxWidth(),
                supportingText = {
                    Text(text = state.password.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Start,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    )
                } ,

                shape = RoundedCornerShape(8.dp),
                singleLine = true,

                )
            
            TextButton(onClick = {

            },modifier = modifier.align(Alignment.End)) {
                Text(text = "Forgot Password")
            }
            
            PrimaryButton(onClick = { events(LoginEvents.OnLogin )}, isLoading = state.isLoading) {
                Text(text = "Login", fontWeight = FontWeight.Bold)
            }


        }

    }

}


