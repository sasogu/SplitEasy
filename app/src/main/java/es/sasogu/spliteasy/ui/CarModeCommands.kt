package es.sasogu.spliteasy.ui

/**
 * Comandos que se pueden ejecutar desde un ordenador con ADB si la caja de
 * coche no muestra las Opciones de desarrollador. No se ejecutan desde la app:
 * solo se muestran para que el usuario pueda copiarlos.
 */
internal const val FREEFORM_ADB_COMMANDS = """adb shell settings put global enable_freeform_support 1
adb shell settings put global force_resizable_activities 1
adb reboot"""
