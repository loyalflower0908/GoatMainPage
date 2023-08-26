package com.example.goat

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.goat.ui.theme.GoatTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //컨텐츠 컬러와 백그라운드 컬러
            var contentColor by remember { mutableStateOf(Color.Black) }
            var backgroundColor by remember { mutableStateOf(Color.White) }
            //다크 모드 트리거
            var isDark by remember {
                mutableStateOf(false)
            }
            //다크 모드에 따른 색상 변경
            if (isDark) {
                contentColor = Color.White
                backgroundColor = Color(0xff121212)
            } else {
                contentColor = Color.Black
                backgroundColor = Color.White
            }
            GoatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = backgroundColor
                ) {
                    //네비게이션 컴포즈를 위한 네비게이션 컨트롤러
                    val navController = rememberNavController()
                    //뒤로가기를 컨트롤하기 위한 명령어
                    var waitTime by remember { mutableStateOf(0L) }
                    BackHandler(enabled = true, onBack = {
                        if (System.currentTimeMillis() - waitTime >= 1600) {
                            waitTime = System.currentTimeMillis()
                            Toast.makeText(
                                this@MainActivity,
                                "뒤로가기 버튼을 한번 더 누르면 종료됩니다.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            finish()
                        }
                    })
                    //네비게이션 컴포즈 구현부
                    //trasition쪽이 애니메이션, composable이 각 화면들(액티비티)/화면은 함수로, route로 화면 state관리
                    //navigate로 화면 이동, popupto로 스택관리
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.fillMaxSize(),
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(500)
                            )
                        }
                    ) {
                        composable("home") {
                            HomeScreen(
                                onNavigateToNext = {
                                    navController.navigate("next") {
                                        popUpTo("next") {
                                            inclusive = true
                                        }
                                    }
                                },
                                contentColor = contentColor,
                                backgroundColor = backgroundColor,
                                switchAct = { isDark = !isDark },
                                isDark = isDark
                            )
                        }
                        ////다음 페이지 관리////////////////
                        composable("next") {
                            NextScreen(
                                backgroundColor = backgroundColor,
                                contentColor = contentColor,
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") {
                                            inclusive = true
                                        }
                                    }
                                })
                        }
                    }
                }
            }
        }
    }
}

//메인 페이지(홈 화면)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    onNavigateToNext: () -> Unit,
    backgroundColor: Color,
    contentColor: Color,
    switchAct: (Boolean) -> Unit,
    isDark: Boolean
) {
    //다크 모드 설정 스위치 트리거
    var switchTrigger by remember { mutableStateOf(isDark) }
    //다크 모드 설정 애니메이션
    val animateBackgroundColor by animateColorAsState(
        targetValue =
        if (switchTrigger) Color(0xff121212) else Color.White,
        animationSpec = tween(800),
        label = "backgroundColor"
    )
    val animateContentColor: Color by animateColorAsState(
        targetValue =
        if (switchTrigger) Color.White else Color.Black,
        animationSpec = tween(1000),
        label = "contentColor"
    )
    //홈버튼, 메신저 버튼 체크
    var homeSelected by remember {
        mutableStateOf(true)
    }
    //풀 화면 다이어로그 트리거 / 할 일 관리, 설정, 개인 프로필
    var showScheduleDialog by remember { mutableStateOf(false) }
    var showSettingDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    // 서랍 상태 트리거
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // 코루틴을 위한 코루틴 스코프
    val scope = rememberCoroutineScope()
    //해당 부분은 다이어로그 3개를 관리하는 명령어.
    if (showSettingDialog) {
        //설정 화면
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { showSettingDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(animateBackgroundColor)
            ) {
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(imageVector = Icons.Outlined.KeyboardArrowLeft,
                            contentDescription = "back",
                            tint = animateContentColor,
                            modifier = Modifier
                                .align(
                                    Alignment.CenterVertically
                                )
                                .size(32.dp)
                                .clickable { showSettingDialog = false })
                        Spacer(modifier = Modifier.size(24.dp, 0.dp))
                        Text(
                            text = "환경설정",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = animateContentColor,
                            modifier = Modifier.align(
                                Alignment.CenterVertically
                            )
                        )
                    }
                    Divider(color = animateContentColor.copy(0.2f))
                    Spacer(modifier = Modifier.size(0.dp, 16.dp))
                    Column(modifier = Modifier.padding(32.dp, 4.dp)) {
                        Row {
                            Text(
                                text = "다크모드",
                                color = animateContentColor,
                                fontSize = 16.sp,
                                modifier = Modifier.align(
                                    Alignment.CenterVertically
                                )
                            )
                            Spacer(modifier = Modifier.size(24.dp, 0.dp))
                            Switch(
                                checked = switchTrigger,
                                onCheckedChange = {
                                    switchTrigger = !switchTrigger
                                    switchAct(switchTrigger)
                                },
                                modifier = Modifier.align(
                                    Alignment.CenterVertically
                                )
                            )
                        }
                    }
                }
            }
        }
    } else if (showScheduleDialog) {
        //할 일 관리 화면
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { showScheduleDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowLeft,
                            contentDescription = "back",
                            tint = contentColor,
                            modifier = Modifier
                                .align(
                                    Alignment.CenterVertically
                                )
                                .size(32.dp)
                                .clickable { showScheduleDialog = !showScheduleDialog }
                        )
                        Spacer(modifier = Modifier.size(24.dp, 0.dp))
                        Text(
                            text = "할 일 관리",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = contentColor,
                            modifier = Modifier.align(
                                Alignment.CenterVertically
                            )
                        )
                    }
                    Divider(color = contentColor.copy(0.2f))
                    Spacer(modifier = Modifier.size(0.dp, 16.dp))
                }
            }
        }
    } else if (showProfileDialog) {
        //개인 페이지 화면///////개인 프로필 깃발
        //여기에 로그인 정보로 꾸며야 함
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { showProfileDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                Column(modifier = Modifier.align(Alignment.TopStart)) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowLeft,
                            contentDescription = "back",
                            tint = contentColor,
                            modifier = Modifier
                                .align(
                                    Alignment.CenterVertically
                                )
                                .size(32.dp)
                                .clickable { showProfileDialog = !showProfileDialog }
                        )
                        Spacer(modifier = Modifier.size(24.dp, 0.dp))
                        Text(
                            text = "프로필 변경",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = contentColor,
                            modifier = Modifier.align(
                                Alignment.CenterVertically
                            )
                        )
                    }
                    Divider(color = contentColor.copy(0.2f))
                    Spacer(modifier = Modifier.size(0.dp, 16.dp))
                }
            }
        }
    }
    //서랍에 대한 코드
    ModalNavigationDrawer(drawerState = drawerState,
        scrimColor = Color.Black.copy(0.6f), drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = backgroundColor,
                drawerContentColor = contentColor
            ) {
                Text(
                    text = "개인 페이지",
                    color = contentColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(12.dp)
                )
                Divider(color = contentColor)
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showProfileDialog = !showProfileDialog }) {
                    Row(
                        modifier = Modifier
                            .padding(20.dp, 30.dp)
                            .align(
                                Alignment.CenterStart
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "",
                            tint = contentColor,
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.size(16.dp, 1.dp))
                        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                            Row {
                                Text(
                                    text = "홍길동 ",
                                    fontSize = 24.sp,
                                    color = contentColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                                Text(
                                    text = "쌤",
                                    fontSize = 24.sp,
                                    color = contentColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                            Spacer(modifier = Modifier.size(1.dp, 4.dp))
                            Text(text = "123456789@naver.com", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = "▶  할 일 관리",
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    },
                    selected = false,
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = backgroundColor,
                        unselectedContainerColor = backgroundColor,
                        selectedTextColor = contentColor,
                        unselectedTextColor = contentColor
                    ),
                    onClick = {
//                        scope.launch {
//                            drawerState.close()
//                        }
                        showScheduleDialog = !showScheduleDialog
                    }
                )
                Box(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(0.dp, 24.dp)
                    ) {
                        Row(modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .clickable { showSettingDialog = !showSettingDialog }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Setting",
                                tint = contentColor,
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(
                                        Alignment.CenterVertically
                                    )
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "설정",
                                fontSize = 16.sp,
                                color = contentColor,
                                modifier = Modifier
                                    .align(
                                        Alignment.CenterVertically
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.size(40.dp))
                        Divider(
                            color = contentColor, modifier = Modifier
                                .height(40.dp)
                                .width(1.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.size(40.dp))
                        Row(modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .clickable { /*로그아웃 명령*/ }) {
                            Icon(
                                imageVector = Icons.Outlined.ExitToApp,
                                contentDescription = "LogOut",
                                tint = contentColor,
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(
                                        Alignment.CenterVertically
                                    )
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "로그아웃",
                                fontSize = 16.sp,
                                color = contentColor,
                                modifier = Modifier.align(
                                    Alignment.CenterVertically
                                )
                            )
                        }
                    }
                }
            }
        }) {
        //홈 화면에 대한 코드들
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                //맨 위의 앱 바
                CenterAlignedTopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "G.O.A.T",
                                modifier = Modifier.align(
                                    Alignment.CenterHorizontally
                                ),
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.size(1.dp))
                            Text(
                                text = "Groupware Of All Teachers",
                                fontSize = 12.sp,
                                modifier = Modifier.align(
                                    Alignment.CenterHorizontally
                                ),
                                color = Color.LightGray,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = if (isDark) Color(0xff121212) else Color(0xFF4CAF50)
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.apply { if (isClosed) open() else close() } } }) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Profile",
                                modifier = Modifier.size(45.dp),
                                tint = Color.White
                            )
                        }
                    })
                Divider()
                //앱 바 아래 화면
                Box(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(targetState = homeSelected, content = {homeSelected ->
                        Box(modifier = Modifier.fillMaxSize()){
                            ///해당 박스가 메인 화면 컨텐츠 구현부.
                            // 주의점은 Center가 Center가 아니라는것.
                            if(homeSelected){
                                Button(
                                    onClick = onNavigateToNext,
                                    modifier = Modifier.align(Alignment.Center)
                                ) {
                                    Text(text = "다음 페이지")
                                }
                            }else{
                                Text(text = "메신저 구현부", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = contentColor,  modifier = Modifier.align(Alignment.Center))
                            }
                        }
                    }, label = "main content")
                    Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                        Divider(thickness = 1.dp, color = Color.Black.copy(0.1f))
                        //하단 바 명령어////////////////////////
                        NavigationBar(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            containerColor = backgroundColor
                        ) {
                            NavigationBarItem(
                                selected = false,
                                onClick = { homeSelected = true },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.Home,
                                        contentDescription = null,
                                        tint = if (homeSelected) Color(0xff2196f3) else if (switchTrigger) contentColor else LocalContentColor.current
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Home",
                                        color = if (homeSelected) Color(0xff2196f3) else if (switchTrigger) contentColor else LocalContentColor.current
                                    )
                                })
                            NavigationBarItem(
                                selected = false,
                                onClick = { homeSelected = false },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.MailOutline,
                                        contentDescription = null,
                                        tint = if (homeSelected) if (switchTrigger) contentColor else LocalContentColor.current else Color(
                                            0xff2196f3
                                        )
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Message",
                                        color = if (homeSelected) if (switchTrigger) contentColor else LocalContentColor.current else Color(
                                            0xff2196f3
                                        )
                                    )
                                })
                        }
                    }
                }
            }
        }
    }
}

//다음 페이지(기능)
@Composable
fun NextScreen(backgroundColor: Color, contentColor: Color, onNavigateToHome: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowLeft,
                    contentDescription = "back",
                    tint = contentColor,
                    modifier = Modifier
                        .align(
                            Alignment.CenterVertically
                        )
                        .size(32.dp)
                        .clickable(onClick = onNavigateToHome)
                )
                Spacer(modifier = Modifier.size(24.dp, 0.dp))
                Text(
                    text = "기능 제목"/*이 부분 수정해주세요*/,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = contentColor,
                    modifier = Modifier.align(
                        Alignment.CenterVertically
                    )
                )
            }
            Divider(color = contentColor.copy(0.2f))
            Spacer(modifier = Modifier.size(0.dp, 16.dp))
        }
        Box(modifier = Modifier.fillMaxSize()){
            // 상단 바 이외의 컨텐츠가 들어갈 자리
            /*이 부분 수정해주세요*/
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GoatTheme {}
}

