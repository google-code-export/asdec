unit uMain;

interface

uses
  Windows, Messages, SysUtils, Variants, Classes, Graphics, Controls, Forms,
  Dialogs, OleCtrls, ShockwaveFlashObjects_TLB, StdCtrls;

type
  TfrmMain = class(TForm)
    flaPreview: TShockwaveFlash;

    procedure FormActivate(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure FormDestroy(Sender: TObject);
    procedure Button1Click(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

  type
  TPipeThread = class(TThread)
  protected
    flashFile:string;
    w:integer;
    h:integer;
    procedure Execute; override;
    procedure displaySWF;
    procedure setPos;
  end;

  TBuf = array[0..255] of byte;
var
  frmMain: TfrmMain;
  k:String;
  t:TPipeThread;
implementation

{$R *.dfm}





function arrToStr(k:TBuf;len:integer):string ;
var s:string;
i:integer;
begin
 s:='';
 for i:=0 to len-1 do
  begin
    if k[i]=0 then
     break;
    s:=s +''+ chr(k[i]);
  end;
  Result:=s;
end;

procedure TPipeThread.displaySWF();
begin
  frmMain.flaPreview.Free;
  frmMain.flaPreview:=nil;
  frmMain.flaPreview:=TShockwaveFlash.Create(frmMain);
  frmMain.flaPreview.Left:=0;
  frmMain.flaPreview.Top:=0;
  frmMain.flaPreview.Width:=self.w;
  frmMain.flaPreview.Height:=self.h;
  frmMain.flaPreview.Parent:=frmMain;
  frmMain.flaPreview.Movie:=flashFile;

  frmMain.flaPreview.Play;
  frmMain.Caption:='set movie:'+flashFile;
  //showmessage('flashmovie:'+flashFile);
end;



procedure TPipeThread.setPos();
var movie:WideString;
begin
  movie:=frmMain.flaPreview.Movie;
  SetWindowPos(frmMain.Handle,0,0,0,self.w,self.h,SWP_SHOWWINDOW);
  frmMain.flaPreview.Parent:=nil;
  frmMain.flaPreview.Left:=0;
  frmMain.flaPreview.Top:=0;
  frmMain.flaPreview.Width:=self.w;
  frmMain.flaPreview.Height:=self.h;
  frmMain.flaPreview.Parent:=frmMain;
  frmMain.flaPreview.Movie:=movie;

  frmMain.Caption:=''+inttostr(self.w)+'x'+inttostr(self.h);
  frmMain.Repaint;
end;

procedure TPipeThread.Execute();
var pipe:cardinal;
numBytesRead:DWORD;
buffer:TBuf;
pipename:PAnsiChar;
len:integer;
cmd:integer;
begin
pipename:=PAnsiChar('\\.\\pipe\ffdec_flashplayer_'+ParamStr(1));
while (not self.Terminated) do
begin
  pipe:=CreateFile(pipename,GENERIC_READ,
		FILE_SHARE_READ + FILE_SHARE_WRITE,
		nil,
		OPEN_EXISTING,
		FILE_ATTRIBUTE_NORMAL,
		0
	);
	numBytesRead:= 0;

  repeat
  begin
        ReadFile(pipe,buffer,1,numBytesRead,nil);
        cmd:=buffer[0];
        if(cmd=1) then
        begin
          ReadFile(pipe,buffer,1,numBytesRead,nil);
          if(numBytesRead>0) then
          begin
            len:=buffer[0];
            ReadFile(pipe,buffer,len,numBytesRead,nil);
            self.flashFile:=arrToStr(buffer,numBytesRead);
            Synchronize(displaySWF);
          end;
        end;
        if(cmd=2) then
        begin
          ReadFile(pipe,buffer,4,numBytesRead,nil);
          self.w:=buffer[0]*256+buffer[1];
          self.h:=buffer[2]*256+buffer[3];
          Synchronize(setPos);
        end;
  end
  until numBytesRead<=0;
  CloseHandle(pipe);


end;

end;

procedure TfrmMain.FormActivate(Sender: TObject);
var target:HWND;
begin
  if(ParamCount>=2) then
  begin
    SetForegroundWindow(HWND(strtoint(ParamStr(2))));
    frmMain.Caption:='FlashPlayerWindow_'+ParamStr(2);
    Application.Title:='FlashPlayerWindow_'+ParamStr(2);
    target:=HWND(strtoint(ParamStr(1)));
    SetWindowLong(frmMain.Handle, GWL_STYLE, 0);
    ShowWindow(frmMain.Handle, SW_SHOW);
    frmMain.Left:=0;
    frmMain.Top:=0;
    windows.SetParent(frmMain.Handle,target);
  end;
end;

procedure TfrmMain.FormCreate(Sender: TObject);
begin
 if(ParamCount>=2) then
 begin
   ShowWindow(Application.Handle, SW_HIDE) ;
   SetWindowLong(Application.Handle, GWL_EXSTYLE, getWindowLong(Application.Handle, GWL_EXSTYLE) or WS_EX_TOOLWINDOW) ;
   ShowWindow(Application.Handle, SW_SHOW);
   t:=TPipeThread.Create(true);
   t.Resume;
 end;
end;

procedure TfrmMain.FormDestroy(Sender: TObject);
begin
t.Free;
end;

procedure TfrmMain.Button1Click(Sender: TObject);
begin

//flaPreview.
//flaPreview.Visible:=true;
end;

end.
