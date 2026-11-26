# تقرير التعديلات — AutomationExerciseTAF

هذه النسخة مبنية على أحدث أرشيف رفعه المستخدم `AutomationExerciseTAF.zip`، وتم الحفاظ على ملفات المشروع الحالية بما فيها `pom.xml` و`src` و`target` و`test-output` وبيانات Allure والتقارير وملفات IntelliJ وGit.

## 1. إصلاحات WebDriver

- إزالة إنشاء ChromeDriver المكرر داخل إعداد Chrome؛ أصبح إنشاء جلسة Chrome في موضع واحد داخل `ChromeFactory.createDriver()` ثم تُطبّق أوامر CDP الاختيارية على الجلسة نفسها.
- جعل `-Dbrowser` يتقدم على `browserType` في ملف الخصائص.
- جعل `-DexecutionType` يتقدم على قيمة `executionType` في ملفات الخصائص في Chrome وEdge وFirefox.
- تحسين رسائل بدء الجلسة وفشل الإنشاء والإغلاق.
- إضافة `getIfAvailable()` لاستعمال Listener بأمان، ورسالة واضحة عند استدعاء `get()` دون جلسة مهيأة.
- تنظيف الجلسة الجزئية عند فشل إنشاء Driver.

## 2. عزل حالة الاختبارات والـ Parallel Execution

- تحويل مرجع `GUIDriver` في `BaseTest` إلى `ThreadLocal<GUIDriver>`؛ لأن TestNG قد يعيد استخدام instance واحد للكلاس عند تشغيل methods بالتوازي.
- تعديل Test Classes لاستخدام `setDriver()` و`getDriver()` بدل متغير instance مشترك.
- ضمان أن جميع `@BeforeMethod` و`@AfterMethod` في Test Classes تستخدم `alwaysRun = true`.
- توحيد التنظيف عبر `BaseTest.closeTestResources()`.
- الإبقاء على قائمة المنتجات داخل `ThreadLocal<List<SelectedProducts>>` وإرجاع snapshot غير قابل للتعديل عند القراءة.
- عزل Faker عبر `ThreadLocal<Faker>`.

## 3. Soft Assertions

- جعل `SoftAssert` وحالة العدّ per-thread داخل `Validation`.
- إضافة `Validation.clear()` لإزالة سياق الـ Thread الحالي.
- إزالة استدعاءات `assertAll()` القديمة من Page Components؛ ويتم تجميع التحققات طوال الاختبار ثم إنهاؤها مرة واحدة بعد Test Method من خلال `TestNGListeners`.
- تحويل غياب العنصر بعد timeout في فحص visibility إلى Soft Assertion فاشل بدل إنهاء الاختبار فورًا بـ TimeoutException.

## 4. Listener وLogging

- تحديث `TestNGListeners` لبدء Validation context لكل Test Method، وإنهاء الـ Soft Assertions بعد الاختبار، ثم التقاط Screenshot وإرفاق Logs وAllure artifacts عند توفر Driver.
- منع Listener من إضافة NullPointerException ثانوي إذا لم يكن Driver موجودًا.
- إضافة overload في `LogsManager` لتسجيل Throwable مع Stack Trace كامل.
- رفع مستوى حفظ File Appender إلى DEBUG، مع إبقاء Console على INFO.
- إزالة طباعة كلمة المرور المولّدة من `RegisterTest`.
- إزالة `System.out` و`printStackTrace` من ملفات Java المصدر التي تمت مراجعتها.
- إضافة/تحسين Logs وصفية داخل Driver factories وPage Components وElement Actions.

## 5. مراجعة أسماء Utils والموارد

تم تنظيم الحزم إلى:

- `utils.data.reader`: `JsonReader`, `PropertyReader`, `ExcelReader`.
- `utils.data.model`: `AccountData`, `PaymentCardData`.
- `utils.data.generator`: `TestDataGenerator`.
- `utils.report`: أدوات Allure ومنها `AllureUtils`.

تطابق أسماء الملفات مع أسماء الأنواع العامة في ملفات Java التي تمت مراجعتها. `ExcelReader` ما زال placeholder فارغًا وغير مستخدم حاليًا، ولم تتم إضافة اعتماد Excel غير موجود في المشروع.

تم تصحيح اسم المورد `test-Uplaod-File.txt` إلى `test-Upload-File.txt` وتحديث مرجعه داخل `ContactUsTests`.

## 6. Maven / TestNG execution

- إزالة اعتماد Surefire على `suiteXmlFiles` وملفات التشغيل `HomeSuite.xml` و`RegressionSuite.xml`.
- حذف ملفات TestNG suite XML من جذر المشروع ومجلد `Test Runner`.
- إبقاء Maven Surefire ليكتشف Test Classes حسب أنماط التسمية الافتراضية.
- تفعيل `failIfNoTests` و`failIfNoSpecifiedTests` لتقليل احتمالية اعتبار عدم اكتشاف الاختبارات نجاحًا.
- أمثلة التشغيل موثقة في `README-AR.md`.

## 7. مسارات Allure وJenkins

المسار المعتمد في `AllureConstants` هو:

```text
test-output/allure-results
```

لذلك يجب ضبط Jenkins > Allure Report > Results > Path على `test-output/allure-results`، وليس `target/test-output/allure-results`.

## 8. مراجعة Page Components وفحوصات السلة

- إزالة الإنهاء المبكر للـ Soft Assertions من مكونات الصفحات؛ أصبحت كل التحققات تُجمع وتُنهى مرة واحدة بعد Test Method بواسطة Listener.
- إضافة رسائل Logs تصف بدء الإجراء ونتيجته وقيم المقارنة في مكونات Cart وCheckout والمنتجات، مع إبقاء تفاصيل البيانات المساعدة على مستوى DEBUG.
- تصحيح فحص سعر الوحدة وإجمالي سطر المنتج في السلة بحيث تتم مقارنة `unitPrice × quantity` مع line total الفعلي.
- فصل عنوان صورة المنتج (`get_product_picture`) عن رابط صفحة تفاصيل المنتج (`product_details`) عند التحقق من روابط السلة.
- جعل قراءة `getSelectedProducts()` تُرجع snapshot غير قابل للتعديل، وعزل حالة المنتجات المختارة حسب Thread.
- حجب قيم الحقول التي تمر عبر `ElementActions.enterText()` و`enterMaskedText()` في Logs؛ لا تُسجّل القيمة المدخلة نفسها.

## 9. حدود التحقق

- تمت مراجعة شجرة الملفات، وتطابق package/imports الداخلية، وتطابق اسم كل ملف Java مع النوع العام المعلن، وتم التحقق من سلامة XML في `pom.xml`.
- لم يتم تنفيذ `mvn test` أو `mvn package` داخل بيئة العمل هذه لأن Maven غير مثبت ولا يوجد Maven Wrapper قابل للتشغيل في الأرشيف.
- `ScreenRecordManager` ما زال lifecycle placeholder؛ اعتماد `video-recorder-testng` موجود لكن تسجيل الفيديو الفعلي لم يُنفذ. يجب إبقاء `recordTests=false` حتى ربط recorder backend.
- لا تُفعّل `parallel=methods` قبل نجاح التشغيل المتسلسل محليًا. تم عزل أهم الحالات المشتركة التي ظهرت في المراجعة، لكن لا يمكن إعلان ضمان كامل للتوازي دون تشغيل الاختبارات في بيئة Selenium فعلية.
