📁 1. Tạo file secrets.properties
Tạo file secrets.properties tại root của project (cùng cấp với settings.gradle và build.gradle).

Cấu trúc thư mục: /your-project-root/ ├── app/ ├── build.gradle ├── secrets.properties ← tạo file này └── settings.gradle
                  /your-project-root/ ├── app/ ├── google-services.json ← thêm file này vào đây

✍️ 2. Thêm thông tin vào secrets.properties
BACKEND_URL =http://10.0.2.2:8080/api/v1/

📁 3. Tạo file ├── app/ ├── res/ ├── value/ ├── mapbox_access_token.xml ← tạo file này

✍️ 4. Thêm thông tin vào mapbox_access_token.xml
<?xml version="1.0" encoding="utf-8"?>
<resources xmlns:tools="http://schemas.android.com/tools">
    <string name="mapbox_access_token" translatable="false" tools:ignore="UnusedResources"> MAP_BOX_KEY </string>
</resources>

I. Cách lấy file google-services.json:
  ✅ Bước 1: Truy cập Firebase Console
    Mở trình duyệt và vào:
    🔗 https://console.firebase.google.com
  
  ✅ Bước 2: Tạo hoặc chọn project Firebase
    Nếu chưa có project:
    Bấm “Add project” (Thêm dự án).
    Nhập tên → Tiếp tục theo hướng dẫn.
    Nếu đã có, chọn vào project bạn muốn dùng.
  
  ✅ Bước 3: Thêm ứng dụng Android vào project
    Ở trang tổng quan của project, nhấn vào biểu tượng Android (</>) để thêm ứng dụng Android mới.
    Nhập:
    Package name (tên gói Android), ví dụ: com.example.myapp
    (Tùy chọn) App nickname, SHA-1 (nếu có)
    ➡️ Nhấn “Register app” (Đăng ký ứng dụng)
  
  ✅ Bước 4: Tải về file google-services.json
    Sau khi đăng ký ứng dụng, bạn sẽ được đưa đến bước tải file.
    Nhấn Download google-services.json

II. Cách lấy MAP_BOX_KEY:
  1. Đăng ký tài khoản Map Box
  2. Vào Tokens ở SlideBar
  3. Create a token
  4. Chọn tick DOWNLOADS:READ
  5. Create Token và copy key
