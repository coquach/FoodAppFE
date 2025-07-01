package com.example.foodapp.ui.screen.setting.privacy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableTargetMarker
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodapp.ui.screen.components.HeaderDefaultView

@Composable
fun PrivacyScreen(
    navController: NavController,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderDefaultView(
            onBack = { navController.navigateUp() },
            text = "Chính sách bảo mật"
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    "Cảm ơn bạn đã sử dụng ứng dụng quản lý và bán hàng online của chúng tôi. " +
                            "Chúng tôi cam kết bảo vệ thông tin cá nhân của người dùng và tuân thủ đầy đủ các quy định về quyền riêng tư và bảo mật.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item { SectionTitle("1. Thông Tin Chúng Tôi Thu Thập") }
            item {
                BulletList(
                    listOf(
                        "Họ tên, số điện thoại, email, địa chỉ giao hàng, tài khoản ngân hàng (nếu cần).",
                        "Tên đăng nhập, mật khẩu (được mã hóa).",
                        "Lịch sử đơn hàng, lượt thích món ăn, phản hồi, đánh giá sản phẩm.",
                        "Thông tin thiết bị: IP, loại máy, hệ điều hành, trình duyệt, vị trí.",
                        "Thông tin thanh toán qua bên thứ ba như VNPAY, Momo, Stripe,…"
                    )
                )
            }

            item { SectionTitle("2. Mục Đích Sử Dụng Thông Tin") }
            item {
                BulletList(
                    listOf(
                        "Quản lý đơn hàng, kho, sản phẩm, và bán hàng online.",
                        "Giao hàng, xác minh khách hàng.",
                        "Cải thiện trải nghiệm và đề xuất món phù hợp.",
                        "Phân tích thống kê để cải thiện dịch vụ.",
                        "Gửi thông báo khuyến mãi (nếu đồng ý).",
                        "Đảm bảo an ninh, tránh gian lận."
                    )
                )
            }

            item { SectionTitle("3. Chia Sẻ Thông Tin Với Bên Thứ Ba") }
            item {
                BulletList(
                    listOf(
                        "Có sự đồng ý của người dùng.",
                        "Cần thiết để giao hàng (ví dụ: cung cấp địa chỉ cho shipper).",
                        "Theo yêu cầu của cơ quan pháp luật."
                    )
                )
            }

            item { SectionTitle("4. Bảo Mật Dữ Liệu") }
            item {
                BulletList(
                    listOf(
                        "Mật khẩu được mã hóa (bcrypt/sha256).",
                        "Dữ liệu truyền qua HTTPS, SSL/TLS.",
                        "Phân quyền nghiêm ngặt theo vai trò.",
                        "Sao lưu định kỳ, cập nhật bảo mật liên tục."
                    )
                )
            }

            item { SectionTitle("5. Quyền Của Người Dùng") }
            item {
                BulletList(
                    listOf(
                        "Xem và cập nhật thông tin cá nhân.",
                        "Yêu cầu xóa tài khoản và dữ liệu.",
                        "Từ chối nhận email quảng cáo."
                    )
                )
            }

            item { SectionTitle("6. Thời Gian Lưu Trữ Dữ Liệu") }
            item {
                BulletList(
                    listOf(
                        "Dữ liệu được lưu trữ trong thời gian tài khoản hoạt động.",
                        "Xóa vĩnh viễn sau 30 ngày nếu người dùng xóa tài khoản."
                    )
                )
            }

            item { SectionTitle("7. Thay Đổi Chính Sách") }
            item {
                Text(
                    "Chính sách có thể thay đổi để phù hợp với quy định pháp luật và hoạt động thực tế. Người dùng sẽ được thông báo trước khi áp dụng thay đổi.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(top = 12.dp),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun BulletList(items: List<String>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach { item ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("• ", fontWeight = FontWeight.Bold)
                Text(item, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PrivacyScreenPreview() {
    PrivacyScreen(navController = NavController(LocalContext.current))
}

