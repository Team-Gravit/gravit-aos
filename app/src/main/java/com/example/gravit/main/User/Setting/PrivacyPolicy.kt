package com.inuappcenter.gravit.main.User.Setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gravit.share.PolicyBody
import com.example.gravit.share.PolicyHeading
import com.example.gravit.share.PolicyHeadline
import com.example.gravit.share.PolicySectionSpace
import com.example.gravit.share.StatusBarStyle
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.inuappcenter.gravit.main.User.TopBar

@Composable
fun PrivacyPolicy(navController: NavController) {
    StatusBarStyle(darkIcons = true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .navigationBarsPadding()
    ) {
        Column {
            TopBar(
                navController,
                title = "개인정보 처리방침"
            )

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                PolicyBody(
                    """
                    Gravit(이하 "서비스")은 「개인정보 보호법」 제30조에 따라 정보주체의 개인정보를 보호하고 이와 관련한 고충을 신속하고 원활하게 처리할 수 있도록 다음과 같이 개인정보 처리방침을 수립·공개합니다.
                    
                    최종 수정일: 2026년 2월 2일
                    """.trimIndent()
                )

                PolicySectionSpace()

                // 1
                PolicyHeading("1. 개인정보의 수집 항목 및 수집 방법")

                PolicyHeadline("1.1 수집 항목")

                PolicyHeadline("회원가입 시 수집하는 정보")

                PolicyBody(
                    """
                    • 필수 항목: 이메일 주소
                    • 소셜 로그인 이용 시: 카카오/구글/네이버 계정 정보(이메일, 프로필 정보)
                    
                    """.trimIndent()
                )

                PolicyHeadline("서비스 이용 과정에서 자동 수집되는 정보")

                PolicyBody(
                    """
                    • 학습 기록(학습한 개념, 문제 풀이 결과)
                    • 리그 포인트(LP) 및 순위 정보
                    • 면접 시뮬레이터 이용 시 음성 데이터
                    • 서비스 이용 기록, 접속 로그, 쿠키, 접속 IP 정보
                    
                    """.trimIndent()
                )

                PolicyHeadline("1.2 수집 방법")

                PolicyBody(
                    """
                    • 회원가입 및 서비스 이용 과정에서 사용자가 직접 입력
                    • 소셜 로그인(카카오, 구글, 네이버) API를 통한 제공
                    • 서비스 이용 과정에서 자동 생성·수집
                    """.trimIndent()
                )

                PolicySectionSpace()

                // 2
                PolicyHeading("2. 개인정보의 수집 및 이용 목적")

                PolicyBody("서비스는 수집한 개인정보를 다음의 목적으로 이용합니다.")
                Spacer(Modifier.height(10.dp))
                PolicyBody(
                    """
                    • 회원 식별 및 본인 확인
                    • CS 학습 콘텐츠 제공 및 학습 기록 관리
                    • 리그 경쟁 시스템 운영(LP 관리, 순위 산정)
                    • 면접 시뮬레이터 기능 제공
                    • 서비스 개선 및 신규 서비스 개발
                    • 문의 및 고객 지원
                    • 부정 이용 방지 및 서비스 안정성 확보
                    """.trimIndent()
                )

                PolicySectionSpace()

                // 3
                PolicyHeading("3. 개인정보의 보유 및 이용 기간")

                PolicyBody(
                    """
                    • 회원 탈퇴 시: 즉시 파기 (단, 관련 법령에 따라 보관이 필요한 경우 예외)
                    • 법령에 따른 보관: 전자상거래법 등 관련 법령에 따라 일정 기간 보관
                        • 계약 또는 청약철회 등에 관한 기록: 5년
                        • 대금결제 및 재화 등의 공급에 관한 기록: 5년
                        • 소비자 불만 또는 분쟁처리에 관한 기록: 3년
                    """.trimIndent()
                )

                PolicySectionSpace()

                // 4
                PolicyHeading("4. 개인정보의 제3자 제공")
                PolicyBody("서비스는 원칙적으로 사용자의 개인정보를 제3자에게 제공하지 않습니다.")
                Spacer(Modifier.height(10.dp))
                PolicyBody("  단, 다음의 경우는 예외로 합니다.")
                Spacer(Modifier.height(10.dp))
                PolicyBody(
                    """
                    • 사용자가 사전에 동의한 경우
                    • 법령의 규정에 의거하거나, 수사 목적으로 법령에 정해진 절차와 방법에 따라 수사기관의 요구가 있는 경우
                    """.trimIndent()
                )

                PolicySectionSpace()

                // 5
                PolicyHeading("5. 개인정보 처리 위탁")

                PolicyBody(
                    "서비스는 원활한 서비스 제공을 위해 다음과 같이 개인정보 처리 업무를 위탁하고 있습니다."
                )
                Spacer(Modifier.height(10.dp))
                PrivacyOutsourcingTable()
                Spacer(Modifier.height(15.dp))
                PolicyBody("위탁 업체 변경 시 개인정보 처리방침을 통해 공지합니다.")
                PolicySectionSpace()

                // 6
                PolicyHeading("6. 정보주체의 권리·의무 및 행사 방법")
                PolicyBody("  사용자는 언제든지 다음의 권리를 행사할 수 있습니다.")
                Spacer(Modifier.height(10.dp))

                PolicyBody(
                    """
                    • 개인정보 열람 요구
                    • 개인정보 정정·삭제 요구
                    • 개인정보 처리 정지 요구
                    • 회원 탈퇴(동의 철회)
                    """.trimIndent()
                )

                Spacer(Modifier.height(8.dp))

                PrivacyEmailText(
                    prefix = "권리 행사는 서비스 내 설정 메뉴 또는 고객센터(",
                    email = "xunssoie@gmail.com",
                    suffix = ")를 통해 가능합니다."
                )

                PolicySectionSpace()

                // 7
                PolicyHeading("7. 개인정보의 파기")

                PolicyBody(
                    "서비스는 개인정보 보유 기간의 경과, 처리 목적 달성 등 개인정보가 불필요하게 되었을 때 지체 없이 해당 개인정보를 파기합니다."
                )
                Spacer(Modifier.height(10.dp))
                PolicyHeadline("파기 절차")

                PolicyBody(
                    "• 이용자가 입력한 정보는 목적 달성 후 내부 방침 및 관련 법령에 따라 일정 기간 저장 후 파기"
                )

                PolicyHeadline("파기 방법")

                PolicyBody(
                    """
                    • 전자적 파일 형태: 복구 불가능한 방법으로 영구 삭제
                    • 종이 문서: 분쇄 또는 소각
                    """.trimIndent()
                )

                PolicySectionSpace()

                // 8
                PolicyHeading("8. 개인정보 보호책임자")

                PolicyBody(
                    "서비스는 개인정보 처리에 관한 업무를 총괄해서 책임지고, 개인정보 처리와 관련한 정보주체의 불만 처리 및 피해구제를 위하여 아래와 같이 개인정보 보호책임자를 지정하고 있습니다."
                )
                Spacer(Modifier.height(10.dp))

                PolicyHeadline("개인정보 보호책임자")

                PolicyBody("• 성명: 한준서")

                PrivacyEmailText(
                    prefix = "• 이메일: ",
                    email = "xunssoie@gmail.com"
                )

                Spacer(Modifier.height(16.dp))

                PolicyBody(
                    "개인정보 침해에 대한 신고나 상담이 필요하신 경우 아래 기관에 문의하실 수 있습니다."
                )

                Spacer(Modifier.height(8.dp))

                PrivacyContactLinks()

                PolicySectionSpace()

                // 9
                PolicyHeading("9. 개인정보 처리방침의 변경")

                PolicyBody(
                    "본 개인정보 처리방침은 법령·정책 또는 보안기술의 변경에 따라 내용의 추가·삭제 및 수정이 있을 시 시행일자 최소 7일 전에 서비스 공지사항을 통해 고지합니다."
                )

                PolicySectionSpace()

                HorizontalDivider(Modifier.fillMaxWidth(), 1.dp, AppColor.divider1)
                PolicySectionSpace()
                PolicyBody(
                    """
                    공고일자: 2026년 2월 2일
                    시행일자: 2026년 2월 2일
                    """.trimIndent()
                )

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
@Composable
private fun PrivacyOutsourcingTable() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = AppColor.divider1
            )
    ) {
        PrivacyTableRow(
            left = "수탁업체",
            right = "위탁 업무 내용",
            isHeader = true
        )

        PrivacyTableRow(
            left = "카카오",
            right = "소셜 로그인 인증"
        )

        PrivacyTableRow(
            left = "구글",
            right = "소셜 로그인 인증"
        )

        PrivacyTableRow(
            left = "네이버",
            right = "소셜 로그인 인증",
            showBottomBorder = false
        )
    }
}

@Composable
private fun PrivacyTableRow(
    left: String,
    right: String,
    isHeader: Boolean = false,
    showBottomBorder: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (showBottomBorder) {
                    Modifier.border(
                        width = 1.dp,
                        color = AppColor.divider1
                    )
                } else {
                    Modifier
                }
            )
    ) {
        Text(
            text = left,
            style = if (isHeader) {
                AppTypography.Headline2
            } else {
                AppTypography.Body2_Reading
            },
            color = AppColor.text2,
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        )

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(IntrinsicSize.Min)
                .background(AppColor.divider1)
        )

        Text(
            text = right,
            style = if (isHeader) {
                AppTypography.Headline2
            } else {
                AppTypography.Body2_Reading
            },
            color = AppColor.text2,
            modifier = Modifier
                .weight(1.5f)
                .padding(12.dp)
        )
    }
}
@Composable
private fun PrivacyEmailText(
    prefix: String,
    email: String,
    suffix: String = ""
) {
    Text(
        text = buildAnnotatedString {
            append(prefix)

            withLink(
                LinkAnnotation.Url(
                    url = "mailto:$email",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = AppColor.Main1,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                )
            ) {
                append(email)
            }

            append(suffix)
        },
        style = AppTypography.Body2_Reading,
        color = AppColor.text2
    )
}
@Composable
private fun PrivacyContactLinks() {
    val linkStyle = TextLinkStyles(
        style = SpanStyle(
            color = AppColor.Main1,
            textDecoration = TextDecoration.Underline
        )
    )

    Text(
        text = buildAnnotatedString {
            append("• 개인정보침해신고센터: (국번없이) 118 (")

            withLink(
                LinkAnnotation.Url(
                    url = "https://privacy.kisa.or.kr/",
                    styles = linkStyle
                )
            ) {
                append("privacy.kisa.or.kr")
            }

            append(")\n")

            append("• 개인정보분쟁조정위원회: (국번없이) 1833-6972 (")

            withLink(
                LinkAnnotation.Url(
                    url = "https://www.kopico.go.kr/",
                    styles = linkStyle
                )
            ) {
                append("www.kopico.go.kr")
            }

            append(")\n")

            append("• 대검찰청 사이버수사과: (국번없이) 1301 (")

            withLink(
                LinkAnnotation.Url(
                    url = "https://www.spo.go.kr/",
                    styles = linkStyle
                )
            ) {
                append("www.spo.go.kr")
            }

            append(")\n")

            append("• 경찰청 사이버안전국: (국번없이) 182 (")

            withLink(
                LinkAnnotation.Url(
                    url = "https://ecrm.cyber.go.kr/",
                    styles = linkStyle
                )
            ) {
                append("ecrm.cyber.go.kr")
            }

            append(")")
        },
        style = AppTypography.Body2_Reading,
        color = AppColor.text2
    )
}
