// // Flutter FCM Integration Example
// // File này chứa code mẫu để tích hợp FCM vào Flutter app

// import 'dart:convert';
// import 'dart:io';
// import 'package:firebase_core/firebase_core.dart';
// import 'package:firebase_messaging/firebase_messaging.dart';
// import 'package:flutter_local_notifications/flutter_local_notifications.dart';
// import 'package:http/http.dart' as http;
// import 'package:device_info_plus/device_info_plus.dart';

// // ==========================================
// // 1. Firebase Background Message Handler
// // ==========================================
// // Đặt ở top-level, ngoài class
// @pragma('vm:entry-point')
// Future<void> _firebaseMessagingBackgroundHandler(RemoteMessage message) async {
//   await Firebase.initializeApp();
//   print('Handling a background message: ${message.messageId}');
// }

// // ==========================================
// // 2. FCM Service Class
// // ==========================================
// class FCMService {
//   static final FCMService _instance = FCMService._internal();
//   factory FCMService() => _instance;
//   FCMService._internal();

//   final FirebaseMessaging _firebaseMessaging = FirebaseMessaging.instance;
//   final FlutterLocalNotificationsPlugin _localNotifications = 
//       FlutterLocalNotificationsPlugin();

//   String? _fcmToken;
//   String? get fcmToken => _fcmToken;

//   // Backend API base URL
//   static const String _baseUrl = 'http://your-backend-url.com';
  
//   // JWT token của user (lấy sau khi login)
//   String? _jwtToken;

//   // ==========================================
//   // Initialize FCM
//   // ==========================================
//   Future<void> initialize(String jwtToken) async {
//     _jwtToken = jwtToken;
    
//     // Request notification permission (iOS)
//     NotificationSettings settings = await _firebaseMessaging.requestPermission(
//       alert: true,
//       badge: true,
//       sound: true,
//       provisional: false,
//     );

//     if (settings.authorizationStatus == AuthorizationStatus.authorized) {
//       print('User granted permission');
//     }

//     // Initialize local notifications
//     await _initLocalNotifications();

//     // Get FCM token
//     await _getFCMToken();

//     // Listen to token refresh
//     _firebaseMessaging.onTokenRefresh.listen((newToken) {
//       _fcmToken = newToken;
//       _sendTokenToServer(newToken);
//     });

//     // Setup message handlers
//     _setupMessageHandlers();
//   }

//   // ==========================================
//   // Initialize Local Notifications
//   // ==========================================
//   Future<void> _initLocalNotifications() async {
//     const AndroidInitializationSettings androidSettings =
//         AndroidInitializationSettings('@mipmap/ic_launcher');

//     const DarwinInitializationSettings iosSettings =
//         DarwinInitializationSettings(
//       requestAlertPermission: true,
//       requestBadgePermission: true,
//       requestSoundPermission: true,
//     );

//     const InitializationSettings initSettings = InitializationSettings(
//       android: androidSettings,
//       iOS: iosSettings,
//     );

//     await _localNotifications.initialize(
//       initSettings,
//       onDidReceiveNotificationResponse: _onNotificationTapped,
//     );

//     // Create notification channel for Android
//     const AndroidNotificationChannel channel = AndroidNotificationChannel(
//       'piano_learner_channel',
//       'Piano Learner Notifications',
//       description: 'Notifications for Piano Learner app',
//       importance: Importance.high,
//     );

//     await _localNotifications
//         .resolvePlatformSpecificImplementation<
//             AndroidFlutterLocalNotificationsPlugin>()
//         ?.createNotificationChannel(channel);
//   }

//   // ==========================================
//   // Get FCM Token
//   // ==========================================
//   Future<void> _getFCMToken() async {
//     try {
//       _fcmToken = await _firebaseMessaging.getToken();
//       if (_fcmToken != null) {
//         print('FCM Token: $_fcmToken');
//         await _sendTokenToServer(_fcmToken!);
//       }
//     } catch (e) {
//       print('Error getting FCM token: $e');
//     }
//   }

//   // ==========================================
//   // Send Token to Backend Server
//   // ==========================================
//   Future<void> _sendTokenToServer(String token) async {
//     if (_jwtToken == null) {
//       print('JWT token is null, cannot send FCM token to server');
//       return;
//     }

//     try {
//       final deviceId = await _getDeviceId();
//       final deviceName = await _getDeviceName();
//       final deviceType = Platform.isAndroid ? 'android' : 'ios';

//       final response = await http.post(
//         Uri.parse('$_baseUrl/api/auth/fcm/token'),
//         headers: {
//           'Authorization': 'Bearer $_jwtToken',
//           'Content-Type': 'application/json',
//         },
//         body: jsonEncode({
//           'token': token,
//           'deviceId': deviceId,
//           'deviceType': deviceType,
//           'deviceName': deviceName,
//         }),
//       );

//       if (response.statusCode == 200) {
//         print('FCM token sent to server successfully');
//       } else {
//         print('Failed to send FCM token: ${response.body}');
//       }
//     } catch (e) {
//       print('Error sending FCM token to server: $e');
//     }
//   }

//   // ==========================================
//   // Delete Token from Server (on logout)
//   // ==========================================
//   Future<void> deleteTokenFromServer() async {
//     if (_fcmToken == null || _jwtToken == null) return;

//     try {
//       final response = await http.delete(
//         Uri.parse('$_baseUrl/api/auth/fcm/token?token=$_fcmToken'),
//         headers: {
//           'Authorization': 'Bearer $_jwtToken',
//         },
//       );

//       if (response.statusCode == 200) {
//         print('FCM token deleted from server');
//       }
//     } catch (e) {
//       print('Error deleting FCM token: $e');
//     }
//   }

//   // ==========================================
//   // Setup Message Handlers
//   // ==========================================
//   void _setupMessageHandlers() {
//     // Foreground messages
//     FirebaseMessaging.onMessage.listen((RemoteMessage message) {
//       print('Got a message whilst in the foreground!');
//       print('Message data: ${message.data}');

//       if (message.notification != null) {
//         _showLocalNotification(message);
//       }
//     });

//     // Background messages
//     FirebaseMessaging.onBackgroundMessage(_firebaseMessagingBackgroundHandler);

//     // Notification opened app from background
//     FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) {
//       print('Notification opened app from background');
//       _handleNotificationNavigation(message.data);
//     });

//     // Check if app was opened from a notification
//     _checkInitialMessage();
//   }

//   // ==========================================
//   // Show Local Notification (Foreground)
//   // ==========================================
//   Future<void> _showLocalNotification(RemoteMessage message) async {
//     const AndroidNotificationDetails androidDetails = AndroidNotificationDetails(
//       'piano_learner_channel',
//       'Piano Learner Notifications',
//       importance: Importance.high,
//       priority: Priority.high,
//       showWhen: true,
//     );

//     const DarwinNotificationDetails iosDetails = DarwinNotificationDetails(
//       presentAlert: true,
//       presentBadge: true,
//       presentSound: true,
//     );

//     const NotificationDetails notificationDetails = NotificationDetails(
//       android: androidDetails,
//       iOS: iosDetails,
//     );

//     await _localNotifications.show(
//       message.hashCode,
//       message.notification?.title,
//       message.notification?.body,
//       notificationDetails,
//       payload: jsonEncode(message.data),
//     );
//   }

//   // ==========================================
//   // Handle Notification Tap
//   // ==========================================
//   void _onNotificationTapped(NotificationResponse response) {
//     if (response.payload != null) {
//       final data = jsonDecode(response.payload!);
//       _handleNotificationNavigation(data);
//     }
//   }

//   // ==========================================
//   // Check Initial Message (App opened from notification)
//   // ==========================================
//   Future<void> _checkInitialMessage() async {
//     RemoteMessage? initialMessage =
//         await FirebaseMessaging.instance.getInitialMessage();

//     if (initialMessage != null) {
//       _handleNotificationNavigation(initialMessage.data);
//     }
//   }

//   // ==========================================
//   // Handle Notification Navigation
//   // ==========================================
//   void _handleNotificationNavigation(Map<String, dynamic> data) {
//     final type = data['type'];
    
//     switch (type) {
//       case 'sheet_favorited':
//         final sheetId = data['sheetId'];
//         // Navigate to sheet detail page
//         // navigatorKey.currentState?.pushNamed('/sheet-detail', arguments: sheetId);
//         print('Navigate to sheet detail: $sheetId');
//         break;
        
//       case 'sheet_rated':
//         final sheetId = data['sheetId'];
//         final rating = data['rating'];
//         // Navigate to sheet detail page
//         print('Navigate to sheet detail: $sheetId with rating: $rating');
//         break;
        
//       case 'song_favorited':
//         final songId = data['songId'];
//         // Navigate to song detail page
//         print('Navigate to song detail: $songId');
//         break;
        
//       case 'song_rated':
//         final songId = data['songId'];
//         final rating = data['rating'];
//         // Navigate to song detail page
//         print('Navigate to song detail: $songId with rating: $rating');
//         break;
        
//       default:
//         print('Unknown notification type: $type');
//     }
//   }

//   // ==========================================
//   // Get Device ID
//   // ==========================================
//   Future<String> _getDeviceId() async {
//     final deviceInfo = DeviceInfoPlugin();
    
//     if (Platform.isAndroid) {
//       final androidInfo = await deviceInfo.androidInfo;
//       return androidInfo.id; // Unique ID for Android
//     } else if (Platform.isIOS) {
//       final iosInfo = await deviceInfo.iosInfo;
//       return iosInfo.identifierForVendor ?? 'unknown';
//     }
    
//     return 'unknown';
//   }

//   // ==========================================
//   // Get Device Name
//   // ==========================================
//   Future<String> _getDeviceName() async {
//     final deviceInfo = DeviceInfoPlugin();
    
//     if (Platform.isAndroid) {
//       final androidInfo = await deviceInfo.androidInfo;
//       return '${androidInfo.brand} ${androidInfo.model}';
//     } else if (Platform.isIOS) {
//       final iosInfo = await deviceInfo.iosInfo;
//       return '${iosInfo.name} ${iosInfo.model}';
//     }
    
//     return 'Unknown Device';
//   }
// }

// // ==========================================
// // 3. Usage Example in main.dart
// // ==========================================
// /*
// void main() async {
//   WidgetsFlutterBinding.ensureInitialized();
  
//   // Initialize Firebase
//   await Firebase.initializeApp();
  
//   runApp(MyApp());
// }

// class MyApp extends StatelessWidget {
//   @override
//   Widget build(BuildContext context) {
//     return MaterialApp(
//       title: 'Piano Learner',
//       home: LoginScreen(),
//     );
//   }
// }

// // After successful login:
// class HomeScreen extends StatefulWidget {
//   final String jwtToken;
  
//   const HomeScreen({required this.jwtToken});
  
//   @override
//   _HomeScreenState createState() => _HomeScreenState();
// }

// class _HomeScreenState extends State<HomeScreen> {
//   @override
//   void initState() {
//     super.initState();
//     // Initialize FCM with JWT token
//     FCMService().initialize(widget.jwtToken);
//   }
  
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(title: Text('Home')),
//       body: Center(child: Text('Welcome!')),
//     );
//   }
// }

// // On logout:
// void logout() async {
//   await FCMService().deleteTokenFromServer();
//   // Clear JWT token and navigate to login
// }
// */

// // ==========================================
// // 4. Required Dependencies (pubspec.yaml)
// // ==========================================
// /*
// dependencies:
//   firebase_core: ^2.24.0
//   firebase_messaging: ^14.7.6
//   flutter_local_notifications: ^16.3.0
//   http: ^1.1.0
//   device_info_plus: ^9.1.1
// */
