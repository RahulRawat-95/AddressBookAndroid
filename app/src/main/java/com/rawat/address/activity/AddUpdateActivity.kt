package com.rawat.address.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.library.address.database.Address
import com.rawat.address.composable.Field
import com.rawat.address.composable.FormState
import com.rawat.address.model.AddressSerialized
import com.rawat.address.ui.theme.AddressTheme
import com.rawat.address.util.Length
import com.rawat.address.util.Required
import com.rawat.address.viewModel.AddUpdateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AddUpdateActivity : BaseActivity() {
    private lateinit var viewModel: AddUpdateViewModel
    private var addressSerialized: AddressSerialized? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(AddUpdateViewModel::class.java)
        addressSerialized = intent.getSerializableExtra(EXTRA_ADDRESS) as? AddressSerialized

        addObservers()

        setContent {
            AddressTheme {
                Surface(color = MaterialTheme.colors.background) {
                    AddUpdateActivityContent(
                        viewModel = viewModel,
                        addressSerialized = addressSerialized
                    ) { map, isDefault ->
                        viewModel.addOrUpdateAddress(
                            addressSerialized = addressSerialized,
                            map = map,
                            isDefault = isDefault
                        )
                    }
                }
            }
        }
    }

    private fun addObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.successfullySubmitted.collect {
                    if (it)
                        finish()
                }
            }
        }
    }

    companion object {
        private const val EXTRA_ADDRESS = "EXTRA_ADDRESS"

        fun newIntent(context: Context, address: Address?): Intent {
            val intent = Intent(context, AddUpdateActivity::class.java)
            if (address != null)
                intent.putExtra(EXTRA_ADDRESS, AddressSerialized(address))
            return intent
        }
    }
}

@Composable
fun AddUpdateActivityContent(
    viewModel: AddUpdateViewModel,
    addressSerialized: AddressSerialized?,
    onSubmit: (Map<String, String>, Boolean) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val state by remember { mutableStateOf(FormState()) }
    val scope = rememberCoroutineScope()
    val checkedState = remember { mutableStateOf(addressSerialized?.is_default ?: false) }
    val showProgress by viewModel.showProgressBar.collectAsState(false)

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row {
                    Text(
                        text = if (addressSerialized == null) "Create Address" else "Update Address",
                        modifier = Modifier.padding(all = 10.dp)
                    )
                }
            })
        },
        scaffoldState = scaffoldState,
        snackbarHost = { scaffoldState.snackbarHostState }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
        ) {
            FormContainer(
                addressSerialized = addressSerialized,
                state = state,
                scope = scope,
                checkedState = checkedState,
                isUpdate = addressSerialized != null,
                onCheckedChange = { checkedState.value = it },
                onSubmit = onSubmit
            )
            LoadingBar(showProgress = showProgress)
        }
    }
}

@Composable
fun FormContainer(
    addressSerialized: AddressSerialized?,
    state: FormState,
    scope: CoroutineScope,
    checkedState: MutableState<Boolean>,
    isUpdate: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onSubmit: (Map<String, String>, Boolean) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .onFocusChanged {

            }
    ) {
        val data = state.getData()
        Form(
            state = state,
            fields = listOf(
                Field(
                    name = AddUpdateViewModel.KEY_FIRSTNAME,
                    label = "First Name",
                    initialText = data[AddUpdateViewModel.KEY_FIRSTNAME] ?: addressSerialized?.firstname,
                    validators = listOf(Required())
                ),
                Field(
                    name = AddUpdateViewModel.KEY_LASTNAME,
                    label = "Last Name",
                    initialText = data[AddUpdateViewModel.KEY_LASTNAME] ?: addressSerialized?.lastname,
                    validators = listOf(Required())
                ),
                Field(
                    name = AddUpdateViewModel.KEY_ADDRESS_1,
                    label = "Address Line 1",
                    initialText = data[AddUpdateViewModel.KEY_ADDRESS_1] ?: addressSerialized?.address1,
                    validators = listOf(Required())
                ),
                Field(
                    name = AddUpdateViewModel.KEY_ADDRESS_2,
                    label = "Address Line 2",
                    initialText = data[AddUpdateViewModel.KEY_ADDRESS_2] ?: addressSerialized?.address2,
                    validators = listOf(Required())
                ),
                Field(
                    name = AddUpdateViewModel.KEY_CITY,
                    label = "City",
                    initialText = data[AddUpdateViewModel.KEY_CITY] ?: addressSerialized?.city,
                    validators = listOf(Required())
                ),
                Field(
                    name = AddUpdateViewModel.KEY_ZIPCODE,
                    label = "Zipcode",
                    initialText = data[AddUpdateViewModel.KEY_ZIPCODE] ?: addressSerialized?.zipcode,
                    validators = listOf(Required(), Length(5))
                ),
                Field(
                    name = AddUpdateViewModel.KEY_PHONE,
                    label = "Phone",
                    initialText = data[AddUpdateViewModel.KEY_PHONE] ?: addressSerialized?.phone,
                    validators = listOf(Required(), Length(10))
                ),
                Field(
                    name = AddUpdateViewModel.KEY_STATENAME,
                    label = "State Name",
                    initialText = data[AddUpdateViewModel.KEY_STATENAME] ?: addressSerialized?.state_name,
                    validators = listOf(Required())
                ),
                Field(
                    name = AddUpdateViewModel.KEY_ALT_PHONE,
                    label = "Alternative Phone",
                    initialText = data[AddUpdateViewModel.KEY_ALT_PHONE] ?: addressSerialized?.phone,
                    validators = listOf(Required())
                ),
                Field(
                    name = AddUpdateViewModel.KEY_COMPANY,
                    label = "Company",
                    initialText = data[AddUpdateViewModel.KEY_COMPANY] ?: addressSerialized?.company,
                    validators = listOf(Required())
                )
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            scope.launch {
                scrollState.animateScrollTo(it.toInt())
            }
        }
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)) {
            Checkbox(
                checked = checkedState.value,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(text = "Make this my default address")
        }
        Button(
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 5.dp)
                .align(Alignment.End),
            onClick = {
                if (state.validate())
                    onSubmit(state.getData(), checkedState.value)
            }
        ) {
            Text(
                if (isUpdate) "Update" else "Submit"
            )
        }
    }
}

@Composable
fun Form(state: FormState, fields: List<Field>, modifier: Modifier, scrollTo: (Float) -> Unit) {
    state.fields = fields

    Column(modifier = modifier) {
        fields.forEach {
            it.Content(modifier = modifier, scrollTo = scrollTo)
        }
    }
}