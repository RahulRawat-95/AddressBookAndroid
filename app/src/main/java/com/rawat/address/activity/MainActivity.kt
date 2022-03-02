package com.rawat.address.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.library.address.database.Address
import com.rawat.address.R
import com.rawat.address.extensions.concatenate
import com.rawat.address.ui.theme.AddressTheme
import com.rawat.address.viewModel.MainViewModel

class MainActivity : BaseActivity() {
    private lateinit var viewModel: MainViewModel
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        viewModel.pauseSyncing = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.startFetching()
        viewModel.sync()

        setContent { MainActivityContent(viewModel = viewModel, onDeleteClick = {
            viewModel.deleteAddress(address = it)
        }, onUpdateClick = {
            viewModel.pauseSyncing = true
            startForResult.launch(AddUpdateActivity.newIntent(this@MainActivity, address = it))
        }, onAddClick = {
            viewModel.pauseSyncing = true
            startForResult.launch(AddUpdateActivity.newIntent(this@MainActivity, address = null))
        }) }
    }
}

@Composable
fun MainActivityContent(
    viewModel: MainViewModel,
    onDeleteClick: (Address) -> Unit,
    onUpdateClick: (Address) -> Unit,
    onAddClick: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val addresses by viewModel.addressFlow.collectAsState(initial = listOf())
    val showProgress by viewModel.showProgressBar.collectAsState(false)

    AddressTheme {
        Surface(color = MaterialTheme.colors.background) {
            Scaffold(
                topBar = {
                    TopAppBar(title = {
                        Row {
                            Text(
                                text = "Addresses",
                                modifier = Modifier.padding(all = 10.dp)
                            )
                        }
                    })
                },
                scaffoldState = scaffoldState,
                snackbarHost = { scaffoldState.snackbarHostState }) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Addresses(addresses = addresses, onDeleteClick = onDeleteClick, onUpdateClick = onUpdateClick, onAddClick = onAddClick)
                    LoadingBar(showProgress = showProgress)
                    SnackbarContainer(snackbarHostState = scaffoldState.snackbarHostState)
                }
            }
        }
    }
}

@Composable
fun Addresses(
    addresses: List<Address>,
    onDeleteClick: (Address) -> Unit,
    onUpdateClick: (Address) -> Unit,
    onAddClick: () -> Unit
) {
    Log.d("dexter", "Addresses called ${addresses.size}")

    if (addresses.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your address book is blank",
                fontSize = 20.sp,
                modifier = Modifier.padding(10.dp)
            )
            Text(
                text = "Kindly add shipping / billing address and enjoy faster checkout",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 80.dp, end = 80.dp, top = 10.dp)
            )
            AddFabComposable(modifier = Modifier, onAddClick = onAddClick)
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(addresses.size) {
                    AddressComposable(address = addresses[it], onDeleteClick = onDeleteClick, onUpdateClick = onUpdateClick)
                }
            }
            AddFabComposable(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 30.dp, end = 30.dp),
                onAddClick = onAddClick
            )
        }
    }
}

@Composable
fun AddFabComposable(modifier: Modifier, onAddClick: () -> Unit) {
    FloatingActionButton(
        backgroundColor = colorResource(id = R.color.primaryYellow),
        modifier = modifier
            .padding(top = 20.dp)
            .size(60.dp),
        onClick = {
            onAddClick()
        }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_plus),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun AddressComposable(
    address: Address,
    onDeleteClick: (Address) -> Unit = {},
    onUpdateClick: (Address) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val addressRef = createRef()
            val iconsRef = createRef()

            Column(
                modifier = Modifier
                    .constrainAs(addressRef) {
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(iconsRef.start)
                        width = Dimension.fillToConstraints
                    }
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OnlyNonBlankText(
                    address.firstname, address.lastname,
                    maxLines = 1
                )
                OnlyNonBlankText(
                    address.address1, address.address2,
                    maxLines = 2
                )
                OnlyNonBlankText(
                    address.city, address.zipcode,
                    maxLines = 1
                )
                OnlyNonBlankText(
                    address.phone,
                    maxLines = 1
                )
            }

            Column(
                modifier = Modifier
                    .constrainAs(iconsRef) {
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                        start.linkTo(addressRef.end)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    }
            ) {
                Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        onClick = {
                            expanded = true
                        },
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .size(width = 40.dp, height = 40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_popup),
                            contentDescription = null,
                            tint = MaterialTheme.colors.onBackground
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(onClick = {
                            expanded = false
                            onDeleteClick(address)
                        }) {
                            Text("Delete")
                        }
                        DropdownMenuItem(onClick = {
                            expanded = false
                            onUpdateClick(address)
                        }) {
                            Text("Update")
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(weight = 1F))

                if (address.is_default == true) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .size(width = 22.dp, height = 23.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        Divider(
            modifier = Modifier
                .background(color = MaterialTheme.colors.onBackground)
                .fillMaxWidth()
                .height(1.dp)
        )
    }
}

@Composable
fun OnlyNonBlankText(vararg text: String?, maxLines: Int, modifier: Modifier = Modifier) {
    val textToDisplay = text.concatenate()
    if (textToDisplay.isNotBlank()) {
        Text(
            text = textToDisplay,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun SnackbarContainer(snackbarHostState: SnackbarHostState) {
    Log.d("dexter", "failure occurred called")
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val snackbarRef = createRef()

        SnackbarHost(
            modifier = Modifier.constrainAs(snackbarRef) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            hostState = snackbarHostState,
            snackbar = {
                Snackbar {
                    Text(text = it.message, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        )
    }
}

@Composable
fun LoadingBar(showProgress: Boolean) {
    Log.d("dexter", "loading bar called")

    if (showProgress) {
        val interactionSource = remember { MutableInteractionSource() }

        Box(modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { }) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultMainPreview() {
    AddressTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colors.background) {
            AddressComposable(
                address = Address(
                    id = 1,
                    firstname = null,
                    lastname = null,
                    address1 = "Address 1",
                    address2 = "Address 2",
                    city = "City",
                    zipcode = "Zip Code",
                    phone = "9899100101",
                    state_name = "State",
                    alternative_phone = "1011009998",
                    company = "Company",
                    state_id = 1,
                    country_id = 1,
                    crud_state = null,
                    is_default = false
                )
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultMainPreviewDark() {
    AddressTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colors.background) {
            AddressComposable(
                address = Address(
                    id = 1,
                    firstname = null,
                    lastname = null,
                    address1 = "Address 1",
                    address2 = "Address 2",
                    city = "City",
                    zipcode = "Zip Code",
                    phone = "9899100101",
                    state_name = "State",
                    alternative_phone = "1011009998",
                    company = "Company",
                    state_id = 1,
                    country_id = 1,
                    crud_state = null,
                    is_default = false
                )
            )
        }
    }
}
