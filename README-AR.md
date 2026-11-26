# AutomationExerciseTAF

إطار أتمتة لاختبارات الويب باستخدام Java 17 وSelenium WebDriver وTestNG وMaven.

## تشغيل الاختبارات باستخدام Maven

لم نعد نعتمد على ملفات TestNG XML لتشغيل الاختبارات. يقوم Maven Surefire باكتشاف Test Classes الموجودة داخل `src/test/java` وفق أنماط التسمية الافتراضية مثل `*Test` و`*Tests`.

```bash
# تشغيل جميع الاختبارات المكتشفة
mvn clean test -Dbrowser=Chrome

# تشغيل Test Class محدد
mvn clean test -Dtest=HomeTests -Dbrowser=Chrome

# تشغيل Test Method محددة
mvn clean test -Dtest=HomeTests#verifySubscriptionInHome -Dbrowser=Chrome

# تشغيل TestNG Group محددة
mvn clean test -Dgroups=smoke -Dbrowser=Chrome
mvn clean test -Dgroups=regression -Dbrowser=Firefox
mvn clean test -Dgroups=sanity -Dbrowser=Chrome

# تشغيل أكثر من Group
mvn clean test -Dgroups=smoke,regression -Dbrowser=Chrome

# استبعاد Group
mvn clean test -DexcludedGroups=TESTER -Dbrowser=Chrome
```

أسماء Groups حساسة لحالة الأحرف. المجموعات الموجودة حاليًا في الكود هي `smoke` و`regression` و`sanity` و`TESTER`. لم تُصنّف جميع الاختبارات ضمن Groups بعد؛ عند عدم استخدام `-Dgroups` يشغّل Maven كل الاختبارات المكتشفة.

القيمة `-Dbrowser` تقبل `Chrome` أو `Firefox` أو `Edge`. وعند عدم تمريرها يستخدم الإطار القيمة `browserType` من `src/main/resources/webApp.properties`.

## إعداد Jenkins Freestyle

استخدم Build Step من نوع **Invoke top-level Maven targets**، واجعل Goals مثل الآتي:

```text
clean test -Dbrowser=${BROWSER} -Dgroups=${TEST_GROUP}
```

أنشئ Parameter باسم `BROWSER` بقيم مثل `Chrome` و`Firefox` و`Edge`، وParameter باسم `TEST_GROUP` بقيم تطابق أسماء Groups الموجودة حرفيًا في `@Test(groups = ...)`.

## Allure

يُحفظ Raw Allure Results في المسار:

```text
test-output/allure-results
```

لذلك يجب ضبط Jenkins > Allure Report > Results > Path على `test-output/allure-results` بالنسبة إلى Workspace.

لتوليد التقرير وفتحه محليًا عند تثبيت Allure CLI:

```bash
allure generate test-output/allure-results -o test-output/full-report --clean
allure open test-output/full-report
```

يُحفظ Trend History في `test-output/allure-history/` ويُعاد نسخه إلى نتائج التشغيل التالي.

## عزل Driver وحالة الاختبارات

- يحتفظ `GUIDriver` بجلسة WebDriver في `ThreadLocal<WebDriver>`.
- يحتفظ `BaseTest` بمرجع `GUIDriver` في `ThreadLocal<GUIDriver>` مستقل لكل Thread؛ لتجنب مشاركة مرجع Driver بين Methods عند تشغيلها بالتوازي.
- تستخدم جميع Test Classes الحالية `@BeforeMethod(alwaysRun = true)` و`@AfterMethod(alwaysRun = true)`.
- يحتفظ `Validation` بحالة `SoftAssert` مستقلة لكل Thread، ويُنهي Listener التحققات مرة واحدة بعد كل Test Method.
- قائمة المنتجات المختارة وFaker معزولتان بحسب Thread.

لا تفعّل TestNG Parallel Execution قبل نجاح التشغيل المتسلسل ومراجعة أي حالة مشتركة إضافية في الاختبارات أو التطبيق.

## مجلدات النتائج

- `target/`: مخرجات Maven Build.
- `test-output/allure-results/`: نتائج Allure الخام.
- `test-output/full-report/`: تقرير Allure الكامل.
- `test-output/reports/`: تقرير Allure بملف HTML واحد.
- `test-output/allure-history/`: سجل Trend History.
- `test-output/Logs/`: ملف Logs للتشغيل الحالي.
- `test-output/screenshots/`: Screenshots.
- `test-output/videos/`: مجلد مخصص لمخرجات الفيديو.
- `test-output/executions/`: أرشيف مستقل لكل Execution.

## قيد معروف

`ScreenRecordManager` حاليًا Lifecycle Placeholder؛ اعتماد `video-recorder-testng` موجود في `pom.xml`، لكن تنفيذ بدء التسجيل وإيقافه غير مربوط بعد. اترك `recordTests=false` في `src/main/resources/video.properties` إلى أن تتم إضافة Recorder Backend فعلي.


## ملاحظات التحقق والتسليم

- تحتوي هذه النسخة على ملفات المصدر ومخرجات `target/` الحالية وبيانات Allure وتقارير التنفيذ والأرشيفات الموجودة داخل `test-output/`.
- أُجريت مراجعات ساكنة لبنية Java والـ imports الداخلية وأسماء الملفات وإعدادات Surefire. لم يُنفّذ Maven Build في بيئة إعداد هذه النسخة لعدم توفر Maven/Wrapper فيها؛ يجب تشغيل أوامر الاختبار أعلاه محليًا قبل اعتبار النسخة متحققة وقت التشغيل.
- تسجيل الفيديو غير مربوط حاليًا بـ Recorder Backend؛ اترك `recordTests=false`.
