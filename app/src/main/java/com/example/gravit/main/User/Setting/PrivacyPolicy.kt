package com.example.gravit.main.User.Setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gravit.ui.theme.pretendard

@Composable
fun PrivacyPolicy(navController: NavController){

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .background(Color.White)
    ){
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color.White)
            ) {
                Row(modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically){
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "닫기",
                        modifier = Modifier
                            .padding(start = 18.dp)
                            .size(20.dp)
                            .clickable {
                                navController.popBackStack()
                            },
                        tint = Color.Black
                    )

                    Spacer(modifier = Modifier.width(18.dp))

                    Text(
                        text = "개인정보 처리방침",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = pretendard
                        ),
                        color = Color(0xFF222222),
                    )
                }
            }
            HorizontalDivider(
                color = Color.Black.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Text(modifier = Modifier.padding(10.dp),
                text = "개인정보처리방침\n" +
                        "\n" +
                        "본 개인정보처리방침은 Gravit(이하 \"서비스 제공자\")가 무료 서비스로 제작한 모바일 디바이스용 Gravit 앱(이하 \"애플리케이션\")에 적용됩니다. 본 서비스는 \"있는 그대로\" 사용하도록 제공됩니다.\n" +
                        "\n" +
                        "정보 수집 및 이용\n" +
                        "\n" +
                        "애플리케이션은 사용자가 다운로드하고 사용할 때 정보를 수집합니다. 수집되는 정보에는 다음과 같은 정보가 포함될 수 있습니다:\n" +
                        "\n" +
                        "- 사용자 디바이스의 인터넷 프로토콜 주소(예: IP 주소)\n" +
                        "- 사용자가 방문한 애플리케이션 페이지, 방문 시간 및 날짜, 해당 페이지에서 소요한 시간\n" +
                        "- 애플리케이션에서 소요한 시간\n" +
                        "- 모바일 디바이스에서 사용하는 운영체제\n" +
                        "\n" +
                        "애플리케이션은 사용자 모바일 디바이스의 정확한 위치 정보를 수집하지 않습니다.\n" +
                        "\n" +
                        "서비스 제공자는 사용자가 제공한 정보를 사용하여 중요한 정보, 필수 공지사항 및 마케팅 프로모션을 제공하기 위해 수시로 연락할 수 있습니다.\n" +
                        "\n" +
                        "더 나은 경험을 위해 애플리케이션을 사용하는 동안 서비스 제공자는 특정 개인식별정보를 제공하도록 요구할 수 있습니다. 서비스 제공자가 요청하는 정보는 본 개인정보처리방침에 설명된 대로 보관되고 사용됩니다.\n" +
                        "\n" +
                        "제3자 접근\n" +
                        "\n" +
                        "서비스 제공자가 애플리케이션 및 서비스 개선에 도움이 되도록 집계되고 익명화된 데이터만 주기적으로 외부 서비스에 전송됩니다. 서비스 제공자는 본 개인정보처리방침에 설명된 방식으로 사용자의 정보를 제3자와 공유할 수 있습니다.\n" +
                        "\n" +
                        "애플리케이션은 데이터 처리에 대한 자체 개인정보처리방침을 가진 제3자 서비스를 이용한다는 점에 유의하시기 바랍니다. 애플리케이션에서 사용하는 제3자 서비스 제공업체의 개인정보처리방침 링크는 다음과 같습니다:\n" +
                        "\n" +
                        "- Google Play Services\n" +
                        "- Firebase Crashlytics\n" +
                        "\n" +
                        "서비스 제공자는 다음과 같은 경우 사용자 제공 정보 및 자동 수집 정보를 공개할 수 있습니다:\n" +
                        "\n" +
                        "- 소환장 또는 유사한 법적 절차를 준수하는 등 법률에 의해 요구되는 경우\n" +
                        "- 자신의 권리를 보호하고, 사용자의 안전이나 타인의 안전을 보호하며, 사기를 조사하거나 정부 요청에 응답하기 위해 공개가 필요하다고 선의로 믿는 경우\n" +
                        "- 서비스 제공자를 대신하여 업무를 수행하고, 공개된 정보를 독립적으로 사용하지 않으며, 본 개인정보처리방침에 명시된 규칙을 준수하기로 동의한 신뢰할 수 있는 서비스 제공업체와의 경우\n" +
                        "\n" +
                        "거부 권리\n" +
                        "\n" +
                        "애플리케이션을 제거하여 모든 정보 수집을 쉽게 중단할 수 있습니다. 모바일 디바이스의 일부로 제공되거나 모바일 애플리케이션 마켓플레이스 또는 네트워크를 통해 제공될 수 있는 표준 제거 프로세스를 사용할 수 있습니다.\n" +
                        "\n" +
                        "데이터 보관 정책\n" +
                        "\n" +
                        "서비스 제공자는 사용자가 애플리케이션을 사용하는 동안과 그 이후 합리적인 기간 동안 사용자 제공 데이터를 보관합니다. 애플리케이션을 통해 제공한 사용자 제공 데이터의 삭제를 원하는 경우 ahh010145@gmail.com으로 연락하시면 합리적인 시간 내에 응답하겠습니다.\n" +
                        "\n" +
                        "아동\n" +
                        "\n" +
                        "서비스 제공자는 13세 미만의 아동으로부터 의도적으로 데이터를 수집하거나 마케팅하기 위해 애플리케이션을 사용하지 않습니다.\n" +
                        "\n" +
                        "애플리케이션은 13세 미만의 누구에게도 해당되지 않습니다. 서비스 제공자는 13세 미만 아동으로부터 의도적으로 개인식별정보를 수집하지 않습니다. 서비스 제공자가 13세 미만의 아동이 개인정보를 제공했다는 것을 발견한 경우, 즉시 서버에서 이를 삭제합니다. 부모 또는 보호자이시고 자녀가 개인정보를 제공했다는 것을 알고 계신 경우, 필요한 조치를 취할 수 있도록 서비스 제공자(ahh010145@gmail.com)에게 연락해 주시기 바랍니다.\n" +
                        "\n" +
                        "보안\n" +
                        "\n" +
                        "서비스 제공자는 사용자 정보의 기밀성 보호에 관심을 가지고 있습니다. 서비스 제공자는 처리하고 유지하는 정보를 보호하기 위해 물리적, 전자적, 절차적 보안장치를 제공합니다.\n" +
                        "\n" +
                        "변경사항\n" +
                        "\n" +
                        "본 개인정보처리방침은 어떤 이유로든 수시로 업데이트될 수 있습니다. 서비스 제공자는 새로운 개인정보처리방침으로 본 페이지를 업데이트하여 개인정보처리방침의 변경사항을 알려드립니다. 지속적인 사용은 모든 변경사항에 대한 승인으로 간주되므로, 변경사항이 있는지 정기적으로 본 개인정보처리방침을 확인하시기 바랍니다.\n" +
                        "\n" +
                        "본 개인정보처리방침은 2025-09-02부터 유효합니다.\n" +
                        "\n" +
                        "귀하의 동의\n" +
                        "\n" +
                        "애플리케이션을 사용함으로써 현재와 향후 수정될 본 개인정보처리방침에 명시된 대로 귀하의 정보 처리에 동의하는 것입니다.\n" +
                        "\n" +
                        "연락처\n" +
                        "\n" +
                        "애플리케이션 사용 중 개인정보와 관련하여 질문이 있거나 관행에 대한 질문이 있는 경우, ahh010145@gmail.com으로 서비스 제공자에게 이메일로 연락하시기 바랍니다."+
                        "\n" +
                        "Privacy Policy\n" +
                        "\n" +
                        "This privacy policy applies to the Gravit app (hereby referred to as \"Application\") for mobile devices that was created by Joon Seo Han (hereby referred to as \"Service Provider\") as a Free service. This service is intended for use \"AS IS\".\n" +
                        "\n" +
                        "Information Collection and Use*\n" +
                        "\n" +
                        "The Application collects information when you download and use it. This information may include information such as\n" +
                        "\n" +
                        "- Your device's Internet Protocol address (e.g. IP address)\n" +
                        "- The pages of the Application that you visit, the time and date of your visit, the time spent on those pages\n" +
                        "- The time spent on the Application\n" +
                        "- The operating system you use on your mobile device\n" +
                        "\n" +
                        "The Application does not gather precise information about the location of your mobile device.\n" +
                        "\n" +
                        "The Service Provider may use the information you provided to contact you from time to time to provide you with important information, required notices and marketing promotions.\n" +
                        "\n" +
                        "For a better experience, while using the Application, the Service Provider may require you to provide us with certain personally identifiable information. The information that the Service Provider request will be retained by them and used as described in this privacy policy.\n" +
                        "\n" +
                        "Third Party Access\n" +
                        "\n" +
                        "Only aggregated, anonymized data is periodically transmitted to external services to aid the Service Provider in improving the Application and their service. The Service Provider may share your information with third parties in the ways that are described in this privacy statement.\n" +
                        "\n" +
                        "Please note that the Application utilizes third-party services that have their own Privacy Policy about handling data. Below are the links to the Privacy Policy of the third-party service providers used by the Application:\n" +
                        "\n" +
                        "- [Google Play Services](https://www.google.com/policies/privacy/)\n" +
                        "- [Firebase Crashlytics](https://firebase.google.com/support/privacy/)\n" +
                        "\n" +
                        "The Service Provider may disclose User Provided and Automatically Collected Information:\n" +
                        "\n" +
                        "- as required by law, such as to comply with a subpoena, or similar legal process;\n" +
                        "- when they believe in good faith that disclosure is necessary to protect their rights, protect your safety or the safety of others, investigate fraud, or respond to a government request;\n" +
                        "- with their trusted services providers who work on their behalf, do not have an independent use of the information we disclose to them, and have agreed to adhere to the rules set forth in this privacy statement.\n" +
                        "\n" +
                        "Opt-Out Rights\n" +
                        "\n" +
                        "You can stop all collection of information by the Application easily by uninstalling it. You may use the standard uninstall processes as may be available as part of your mobile device or via the mobile application marketplace or network.\n" +
                        "\n" +
                        "Data Retention Policy\n" +
                        "\n" +
                        "The Service Provider will retain User Provided data for as long as you use the Application and for a reasonable time thereafter. If you'd like them to delete User Provided Data that you have provided via the Application, please contact them at ahh010145@gmail.com and they will respond in a reasonable time.\n" +
                        "\n" +
                        "Children\n" +
                        "\n" +
                        "The Service Provider does not use the Application to knowingly solicit data from or market to children under the age of 13.\n" +
                        "\n" +
                        "The Application does not address anyone under the age of 13. The Service Provider does not knowingly collect personally identifiable information from children under 13 years of age. In the case the Service Provider discover that a child under 13 has provided personal information, the Service Provider will immediately delete this from their servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact the Service Provider (ahh010145@gmail.com) so that they will be able to take the necessary actions.\n" +
                        "\n" +
                        "Security\n" +
                        "\n" +
                        "The Service Provider is concerned about safeguarding the confidentiality of your information. The Service Provider provides physical, electronic, and procedural safeguards to protect information the Service Provider processes and maintains.\n" +
                        "\n" +
                        "Changes\n" +
                        "\n" +
                        "This Privacy Policy may be updated from time to time for any reason. The Service Provider will notify you of any changes to the Privacy Policy by updating this page with the new Privacy Policy. You are advised to consult this Privacy Policy regularly for any changes, as continued use is deemed approval of all changes.\n" +
                        "\n" +
                        "This privacy policy is effective as of 2025-09-02\n" +
                        "\n" +
                        "Your Consent\n" +
                        "\n" +
                        "By using the Application, you are consenting to the processing of your information as set forth in this Privacy Policy now and as amended by us.\n" +
                        "\n" +
                        "Contact Us\n" +
                        "\n" +
                        "If you have any questions regarding privacy while using the Application, or have questions about the practices, please contact the Service Provider via email at ahh010145@gmail.com."
                ,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = pretendard
                ),
                color = Color.Black,
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PrivacyPolicyPreview() {
    // 프리뷰용 NavController 생성
    val navController = rememberNavController()
    PrivacyPolicy(navController = navController)
}