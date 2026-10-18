# Chính sách điều phối chuyển động (Motion Policy)

Tài liệu này định nghĩa mô hình logic điều phối chuyển động duy nhất trong ứng dụng (**MotionPolicy**). Chính sách này là kiến trúc logic thuần túy (Pure Logic), độc lập với Android Framework và có thể kiểm thử đơn vị tự động trên JVM (JVM Unit Test) mà không phụ thuộc vào thiết bị thật hay Robolectric.

---

## 1. Mô hình Logic & Biến số đầu vào (Input State)

Hành vi chuyển động của bất kỳ thành phần nào trong ứng dụng được quyết định bởi 3 thông số đầu vào:

1. `systemAnimationsEnabled: Boolean`  
   - Đọc từ cài đặt hệ điều hành Android (`Settings.Global.ANIMATOR_DURATION_SCALE != 0f`).  
   - Trả về `false` khi người dùng bật chế độ "Giảm hiệu ứng động" (Reduce Motion / Remove animations) hoặc chế độ tiết kiệm pin nghiêm ngặt.
2. `userAdvancedEffectsEnabled: Boolean`  
   - Cài đặt cục bộ của người dùng trong màn hình Cài đặt ứng dụng (công tắc "Hiệu ứng nâng cao" cho Gói B).  
   - Lưu trữ cục bộ (`SharedPreferences` / DataStore), độc lập với tài khoản và vai trò người dùng.
3. `systemHapticEnabled: Boolean`  
   - Đọc từ cài đặt phản hồi xúc giác hệ thống (`Settings.System.HAPTIC_FEEDBACK_ENABLED != 0`).  
   - Quyết định việc phát xung rung cho FX-03, tôn trọng tuyệt đối lựa chọn của người dùng.

> [!NOTE]
> Hiệu ứng Predictive Back (trước đây là FX-05) đã được loại bỏ hoàn toàn theo yêu cầu của USER. Ứng dụng sử dụng hành vi điều hướng Back tiêu chuẩn của Fragment / AndroidX Activity.

---

## 2. Quy tắc quyết định (Decision Rules)

- **Quy tắc 1 (Hệ thống là ưu tiên tối cao):**  
  Nếu `systemAnimationsEnabled == false`:
  - **Toàn bộ hiệu ứng Gói A (FX-01 → FX-04) và Gói B (FX-05 → FX-11) đều bị tắt chuyển động**, chuyển ngay lập tức sang **phương án tĩnh** (Static Fallback, thời lượng duration = 0 ms).
  - Công tắc "Hiệu ứng nâng cao" trong màn hình Cài đặt **vẫn hiển thị** nhưng ở trạng thái **vô hiệu hóa tương tác (disabled)** kèm dòng chú thích `@string/settings_effects_disabled_system_note`.
  - Haptic feedback (FX-03) chỉ kích hoạt nếu `systemHapticEnabled == true`, không cưỡng bức rung.
- **Quy tắc 2 (Gói A - Luôn bật):**  
  Chạy khi và chỉ khi `systemAnimationsEnabled == true`. Không bị ảnh hưởng bởi công tắc cài đặt Gói B của người dùng.
- **Quy tắc 3 (Gói B - Tùy chọn nâng cao):**  
  Chạy khi và chỉ khi `systemAnimationsEnabled == true && userAdvancedEffectsEnabled == true`.  
  Nếu `userAdvancedEffectsEnabled == false`, toàn bộ hiệu ứng FX-05 đến FX-11 chuyển về phương án tĩnh.
- **Quy tắc 4 (Phản hồi xúc giác Haptic - FX-03):**  
  Chạy khi và chỉ khi `systemHapticEnabled == true`. Không rung khi thiết bị đang ở chế độ im lặng hoặc đã tắt rung hệ thống.

---

## 3. Bảng chân trị điều phối (Truth Table - 8 tổ hợp)

Bảng chân trị dưới đây mô tả đầy đủ 8 trường hợp tổ hợp logic làm cơ sở đặc tả test case cho Developer/Tester:

| Tổ hợp (#) | `systemAnimationsEnabled` | `userAdvancedEffectsEnabled` | `systemHapticEnabled` | Gói A (FX-01, 02, 04) | Haptic FX-03 | Gói B (FX-05 → FX-11) | Trạng thái công tắc Cài đặt | Ghi chú ngữ cảnh thực tế |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---|:---|
| **TC-01** | `true` | `true` | `true` | **BẬT** | **RUNG** | **BẬT** | Cho phép gạt, Trạng thái: ON | Trải nghiệm đầy đủ nhất |
| **TC-02** | `true` | `true` | `false` | **BẬT** | **TẮT** | **BẬT** | Cho phép gạt, Trạng thái: ON | Đầy đủ chuyển động, thiết bị tắt rung |
| **TC-03** | `true` | `false` | `true` | **BẬT** | **RUNG** | **TẮT** (Tĩnh) | Cho phép gạt, Trạng thái: OFF | Người dùng chủ động tắt Gói B |
| **TC-04** | `true` | `false` | `false` | **BẬT** | **TẮT** | **TẮT** (Tĩnh) | Cho phép gạt, Trạng thái: OFF | Chỉ chạy Gói A cơ bản, không rung |
| **TC-05** | `false` | `true` | `true` | **TẮT** (Tĩnh) | **RUNG** | **TẮT** (Tĩnh) | Vô hiệu hóa (Disabled), Có note | Máy bật "Giảm hiệu ứng động", vẫn bật rung |
| **TC-06** | `false` | `true` | `false` | **TẮT** (Tĩnh) | **TẮT** | **TẮT** (Tĩnh) | Vô hiệu hóa (Disabled), Có note | Máy bật tiết kiệm pin nghiêm ngặt |
| **TC-07** | `false` | `false` | `true` | **TẮT** (Tĩnh) | **RUNG** | **TẮT** (Tĩnh) | Vô hiệu hóa (Disabled), Có note | Giảm chuyển động hệ thống, Gói B tắt |
| **TC-08** | `false` | `false` | `false` | **TẮT** (Tĩnh) | **TẮT** | **TẮT** (Tĩnh) | Vô hiệu hóa (Disabled), Có note | Toàn bộ phương án tĩnh, không rung |

---

## 4. Định nghĩa Interface & Cấu trúc kiểm thử (JVM Testable)

Mô hình hướng dẫn triển khai mã nguồn đảm bảo kiểm thử độc lập:

```java
// Logic thuần túy - Không phụ thuộc android.content.Context
public class MotionPolicy {

    public static boolean shouldRunPackageA(boolean systemAnimationsEnabled) {
        return systemAnimationsEnabled;
    }

    public static boolean shouldRunPackageB(boolean systemAnimationsEnabled, boolean userAdvancedEffectsEnabled) {
        return systemAnimationsEnabled && userAdvancedEffectsEnabled;
    }

    public static boolean shouldPerformHaptic(boolean systemHapticEnabled) {
        return systemHapticEnabled;
    }

    public static boolean isSettingSwitchEnabled(boolean systemAnimationsEnabled) {
        return systemAnimationsEnabled;
    }
}
```

### Tiêu chí kiểm thử tự động (Unit Test Acceptance)
1. **Kiểm thử bất biến (Invariant):** Bất cứ khi nào `systemAnimationsEnabled == false`, kết quả trả về của `shouldRunPackageA` và `shouldRunPackageB` bắt buộc phải là `false`.
2. **Kiểm thử công tắc (Settings Switch Invariant):** Khi `systemAnimationsEnabled == false`, `isSettingSwitchEnabled` bắt buộc trả về `false` để UI khóa tương tác công tắc.
3. **Kiểm thử Haptic độc lập:** `shouldPerformHaptic` trả về chính xác theo giá trị `systemHapticEnabled`, không phụ thuộc vào `systemAnimationsEnabled`.
