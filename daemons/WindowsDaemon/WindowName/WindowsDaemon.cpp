#include "pch.h"
#include <iostream>
#include <windows.h>
#include <winuser.h>
#include <windef.h>
#include <chrono>
#include <thread>
#include <vector>
#include <string>
#include <iostream>
#include <libloaderapi.h>
#include <tchar.h>
#include <Psapi.h>
#include <ctime>

const int INACTIVE_SECONDS = 5;
const int MAX_NAME = 1000;
static POINT last_mouse_move;
static TCHAR last_window_name[MAX_NAME] = {0};

bool mouseMoved() {
	POINT p;
	if (GetCursorPos(&p))
	{
		if (last_mouse_move.x != p.x || last_mouse_move.y != p.y) {
			last_mouse_move = p;
			return true;
		}

	}

	return false;
}

int checkKeyPress() {
	char keys[256];
	int count = 0;

	for (int i = 0; i < 256; i++) {
		keys[i] = (char)(GetAsyncKeyState(i) >> 8);
		if (keys[i]) {
			count++;
		}
	}

	return count;
}

bool getFocusedWindow()
{
	HWND handle = GetForegroundWindow();
	TCHAR buffer[MAX_NAME] = { 0 };
	DWORD dwProcId = 0;

	GetWindowThreadProcessId(handle, &dwProcId);

	HANDLE hProc = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, FALSE, dwProcId);
	GetProcessImageFileName((HMODULE)hProc, buffer, MAX_NAME);
	CloseHandle(hProc);

	bool name_changed = false;
	for (int i = 0; i < MAX_NAME; i++) {
		if (i == 0 && buffer[i] == 0) {
			// Don't update me with a null window name
			break;
		}
		if (buffer[i] != last_window_name[i]) {
			name_changed = true;
			last_window_name[i] = buffer[i];
		}
	}

	return name_changed;
}

int main()
{
	std::time_t last_action = std::time(0);
	bool inactive = false;

	while (1) {
		std::time_t curr_time = std::time(0);

		int num = checkKeyPress();
		if (num > 0) {
			last_action = curr_time;
		}
		if (mouseMoved()) {
			last_action = curr_time;
		}
		
		bool window_changed = getFocusedWindow();
		if (window_changed) {
			std::wcout << "window: " << last_window_name << std::endl;
		}

		if (curr_time - last_action > INACTIVE_SECONDS && !inactive) {
			std::cout << "state: inactive" << std::endl;
			inactive = !inactive;
		}
		if (curr_time - last_action < INACTIVE_SECONDS && inactive) {
			std::cout << "state: active" << std::endl;
			inactive = !inactive;
		}

		std::this_thread::sleep_for(std::chrono::milliseconds(10));
	}
}