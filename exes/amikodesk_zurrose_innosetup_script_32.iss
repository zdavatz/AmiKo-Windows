; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppFolder "07_amikodesk_zurrose_exe32"
#define MyAppName "AmiKo Desktop ZR"
#define MyVersion "1.3.0"
#define MyPublisher "ywesee GmbH"
#define MyAppExe "amikodeskzr.exe"
#define MyAppURL "http://www.ywesee.com/AmiKo/Desktop"
#define MyWorkingDir = "E:\Projects\Pharmax\AmiKoWindows"

[Code]
function IsRegularUser(): Boolean;
begin
  Result := not (IsAdminLoggedOn or IsPowerUserLoggedOn);
end;
function DefDirRoot(Param: String): String;
begin
  if IsRegularUser then
    Result := ExpandConstant('{localappdata}')
  else
    Result := ExpandConstant('{pf}')
end;

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{4A7BDD68-8150-4152-92E4-84439DBA1789}
AppName={#MyAppName}
AppVersion={#MyVersion}
AppVerName={#MyAppName} {#MyVersion}
AppPublisher={#MyPublisher}
AppPublisherURL=http://www.ywesee.com
AppSupportURL=http://www.ywesee.com
AppUpdatesURL=http://www.ywesee.com
PrivilegesRequired=none
DefaultDirName={code:DefDirRoot}\{#MyAppName}
DefaultGroupName={#MyAppName}
OutputDir={#MyWorkingDir}\output
OutputBaseFilename=amikodeskzr_setup_32bit
SetupIconFile={#MyWorkingDir}\icons\amiko_icon.ico
Compression=lzma
SolidCompression=yes
VersionInfoDescription={#MyAppName} Setup
VersionInfoVersion={#MyVersion}
VersionInfoCompany={#MyPublisher}
CloseApplications=yes

[Languages]
Name: "german"; MessagesFile: "compiler:Languages\German.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"

[Files]
Source: "{#MyWorkingDir}\exes\{#MyAppFolder}\{#MyAppExe}"; DestDir: "{app}"; Flags: ignoreversion
Source: "{#MyWorkingDir}\exes\{#MyAppFolder}\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExe}"; IconFileName: "{app}\icons\amiko_icon.ico"
Name: "{group}\{cm:ProgramOnTheWeb,{#MyAppName}}"; Filename: "{#MyAppURL}"
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExe}"; IconFileName: "{app}\icons\amiko_icon.ico"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExe}"; Description: "{cm:LaunchProgram,{#MyAppName}}"; Flags: nowait postinstall skipifsilent
