package com.example.proyectopdm2025_gt03_grupo5_gestion_de_gastos_personales_mensuales;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
public class TareaWorker extends Worker{
    public TareaWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    @NonNull
    @Override
    public Result doWork() {
        Log.d("TareaWorker", "¡Worker ejecutado correctamente!");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "recordatorio_channel",
                    "Recordatorios",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "recordatorio_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Recordatorio")
                .setContentText("¡Este es un recordatorio automático de tarea en segundo plano!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        // ✅ Verificar permiso solo si estamos en Android 13 o superior
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                notificationManager.areNotificationsEnabled()) {
            try {
                notificationManager.notify(1001, builder.build());
            } catch (SecurityException e) {
                Log.e("TareaWorker", "No se puede mostrar la notificación. Permiso denegado.", e);
                return Result.failure();
            }
        } else {
            Log.w("TareaWorker", "No se tienen permisos para mostrar notificaciones.");
            return Result.failure();
        }

        return Result.success();
    }
}
