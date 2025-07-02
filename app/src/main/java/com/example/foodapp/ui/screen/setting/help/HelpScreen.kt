package com.example.foodapp.ui.screen.setting.help

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodapp.ui.screen.components.HeaderDefaultView

@Composable
fun HelpScreen(
    navController: NavController,
    typeRole: String,
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
            text = "Hướng dẫn"
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (typeRole) {
                "customer" -> {
                    HelpSection(
                        title = "1. Đăng ký / Đăng nhập", content = """
- Đăng ký: Bấm vào nút “Đăng ký”, nhập họ tên, số điện thoại, email và mật khẩu.
- Đăng nhập: Nhập số điện thoại/email và mật khẩu để truy cập tài khoản.
- Có thể đăng nhập nhanh bằng Google.
            """.trimIndent()
                    )

                    HelpSection(
                        title = "2. Xem thực đơn (Menu)", content = """
- Trên màn hình chính, bạn sẽ thấy danh sách món ăn theo danh mục (Món chính, món phụ,...)
- Bấm vào từng món để xem chi tiết: hình ảnh, mô tả, giá, thành phần.
- Sử dụng tìm kiếm để tìm món ăn theo tên.
            """.trimIndent()
                    )

                    HelpSection(
                        title = "3. Đặt hàng online", content = """
B1: Thêm món ăn vào giỏ hàng
- Từ món ăn ở màn hình chính → Nhấn “Thêm vào giỏ hàng”.
- Vào biểu tượng Giỏ hàng ở góc trên.
- Kiểm tra danh sách món, số lượng và tổng tiền.

B2: Chọn phương thức thanh toán
- Tiền mặt
- Thanh toán online (sẽ tích hợp sau)

B3: Nhập địa chỉ giao hàng
- Chọn từ danh sách địa chỉ đã lưu hoặc thêm mới.
- Có thể ghim vị trí trên bản đồ để giao hàng chính xác hơn.

B4: Xác nhận đơn hàng
- Kiểm tra thông tin → Bấm “Đặt hàng”.
- Đơn hàng sẽ được gửi tới hệ thống và bạn nhận được thông báo trạng thái.
            """.trimIndent()
                    )

                    HelpSection(
                        title = "4. Theo dõi trạng thái đơn hàng", content = """
- Vào mục Đơn hàng của tôi để xem:
    • Đã tiếp nhận
    • Đang chuẩn bị
    • Đang giao
    • Đã hoàn tất
            """.trimIndent()
                    )

                    HelpSection(
                        title = "5. Đánh giá & phản hồi", content = """
- Sau khi nhận hàng, bạn có thể:
    • Đánh giá món ăn (sao + bình luận)
    • Gửi phản hồi nếu có vấn đề
            """.trimIndent()
                    )

                    HelpSection(
                        title = "6. Chat box AI hỗ trợ", content = """
- Nhấn vào icon Chat box ở góc dưới bên trái tại màn hình chính
- Nhập câu hỏi và gửi, chat box sẽ hỗ trợ bạn
- Lưu ý: Chat box chỉ hỗ trợ trả lời câu hỏi liên quan đến quán ăn cũng như app
            """.trimIndent()
                    )

                    HelpSection(
                        title = "7. Tài khoản & cài đặt", content = """
- Vào mục Tài khoản để:
    • Chọn chế độ sáng tối
    • Sửa thông tin cá nhân
    • Đổi mật khẩu
    • Xem lịch sử đơn hàng
    • Đăng xuất
            """.trimIndent()
                    )


                }

                "staff" -> {
                    HelpSection(
                        title = "1. Đăng nhập hệ thống", content = """
- Bước 1: Mở ứng dụng và chọn chức năng “Login”.
- Bước 2: Nhập tài khoản và mật khẩu được cấp để đăng nhập vào hệ thống.
""".trimIndent()
                    )

                    HelpSection(
                        title = "2. Quản lý đơn hàng (Order Management)", content = """
Tạo đơn hàng mới:
- Chọn chức năng “Create Order”.
- Chọn món ăn (Select Food) và áp dụng voucher nếu có (Apply Voucher).
- Xem lại thông tin đơn và xác nhận đặt.

Xem chi tiết đơn hàng:
- Vào chức năng “View Detail Order” để kiểm tra chi tiết đơn đã tạo.

Cập nhật trạng thái đơn:
- Vào “Update Status Order” để cập nhật trạng thái xử lý đơn như: Đang chuẩn bị, Đã giao cho shipper,...
- Xác nhận đơn hàng từ phía khách nếu là đơn online.
""".trimIndent()
                    )

                    HelpSection(
                        title = "3. Quản lý phiếu xuất (Export Management)", content = """
Tạo phiếu xuất kho:
- Chọn nguyên liệu từ view tồn kho.
- Nhập thông tin số lượng và xác nhận.

Cập nhật hoặc xoá phiếu xuất:
- Chỉ được cập nhật hoặc xoá phiếu xuất kho trong ngày.
""".trimIndent()
                    )

                    HelpSection(
                        title = "4. Xem kho nguyên liệu (Inventory Management)", content = """
- Chọn “View Inventory” để xem số lượng tồn kho hiện tại của các nguyên liệu.
""".trimIndent()
                    )

                }

                "admin" -> {
                    HelpSection(
                        title = "1. Đăng nhập hệ thống", content = """
        - Truy cập ứng dụng bằng tài khoản admin được cấp.
        - Nhập email / tài khoản quản trị và mật khẩu.
        """.trimIndent()
                    )

                    HelpSection(
                        title = "2. Quản lý Món ăn", content = """
        - Vào mục “Món ăn” hoặc “Sản phẩm”.
        
        Thêm món mới:
        - Nhập tên, mô tả, giá, hình ảnh, loại món (ví dụ: đồ ăn, đồ uống…).

        Cập nhật món ăn:
        - Thay đổi giá, số lượng còn lại, trạng thái hiển thị (hiện/tạm ẩn).

        Xóa món ăn:
        - Nếu món không còn phục vụ.

        📷 Ảnh món cần tối thiểu 1 ảnh, định dạng JPG/PNG.
        """.trimIndent()
                    )

                    HelpSection(
                        title = "3. Quản lý đơn hàng", content = """
        - Vào mục “Đơn hàng” để theo dõi toàn bộ đơn từ khách hàng.

        Các trạng thái đơn:
        - Đang chờ xử lý: Mới được đặt, chờ xác nhận.
        - Đã xác nhận: Bếp đang chuẩn bị món.
        - Đang giao: Shipper đang vận chuyển.
        - Hoàn tất / Đã hủy: Đơn đã hoàn thành hoặc bị hủy.
        """.trimIndent()
                    )

                    HelpSection(
                        title = "4. Quản lý doanh thu", content = """
        - Vào mục “Thống kê” hoặc “Báo cáo”:
        - Xem doanh thu theo ngày, tháng.
        - Xem số lượng món bán theo danh mục.
        """.trimIndent()
                    )

                    HelpSection(
                        title = "5. Quản lý nhân viên", content = """
        - Thêm, xoá, sửa nhân viên.
        - Xem thông tin, tính lương nhân viên.
        """.trimIndent()
                    )

                    HelpSection(
                        title = "6. Quản lý nhập hàng", content = """
        - Thêm, xoá, sửa nhà cung cấp.
        - Quản lý đơn vị tính, nguyên liệu.
        - Quản lý nhập kho và xem tồn kho nguyên liệu.
        """.trimIndent()
                    )

                    HelpSection(
                        title = "7. Quản lý bàn", content = """
        - Thêm, xoá, sửa bàn.
        - Quản lý trạng thái bàn (đang dùng, trống, đã đặt…).
        """.trimIndent()
                    )

                    HelpSection(
                        title = "8. Quản lý khuyến mãi", content = """
        - Tạo mã giảm giá: phần trăm hoặc số tiền.
        - Thiết lập điều kiện áp dụng (số lượng, thời gian…).
        """.trimIndent()
                    )
                }

                "shipper" -> {
                    HelpSection(
                        title = "1. Đăng nhập hệ thống", content = """
        - Bước 1: Mở ứng dụng và chọn chức năng “Login”.
        - Bước 2: Nhập tài khoản và mật khẩu được cung cấp để đăng nhập vào tài khoản Shipper.
        """.trimIndent()
                    )

                    HelpSection(
                        title = "2. Quản lý đơn giao hàng", content = """
        Nhận đơn hàng:
        - Vào chức năng “Receive Order” để nhận đơn hàng được phân công giao.
        - Kiểm tra thông tin đơn hàng, địa chỉ giao, số điện thoại khách hàng.

        Thực hiện giao hàng:
        - Giao hàng theo thông tin đã nhận từ đơn.
        - Có thể liên hệ khách hàng nếu cần xác nhận.

        Hoàn tất đơn hàng:
        - Sau khi giao thành công, vào “Commit Order” để đánh dấu đơn hàng đã hoàn tất và cập nhật hệ thống.
        """.trimIndent()
                    )

                    HelpSection(
                        title = "3. Xem bản đồ và chỉ đường", content = """
        - Sau khi nhận đơn hàng, vào chi tiết đơn để xem địa chỉ giao hàng.
        - Nhấn vào nút “Xem bản đồ” hoặc biểu tượng định vị, ứng dụng sẽ mở Google Maps hoặc app bản đồ tương ứng.
        - Hệ thống sẽ định vị vị trí hiện tại của bạn và hiển thị lộ trình ngắn nhất.
        - Trong quá trình di chuyển, bạn có thể mở lại bản đồ để theo dõi đường đi và cập nhật thời gian dự kiến đến nơi.

        🔔 Lưu ý: Hãy đảm bảo rằng thiết bị của bạn đã bật GPS và cấp quyền truy cập vị trí cho ứng dụng.
        """.trimIndent()
                    )
                }
            }
        }
    }
}

@Composable
fun HelpSection(title: String, content: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 20.sp
        )
    }
}



