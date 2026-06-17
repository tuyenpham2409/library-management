#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Render UML diagrams for the library report using Pillow (DejaVuSans, VN-capable)."""
import math
from PIL import Image, ImageDraw, ImageFont

S = 2  # supersample for crisp text
FREG = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"
FBLD = "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"

# palette
C_READER = "#DCE9F7"; C_LIB = "#FCE9D2"; C_ADMIN = "#E8DCF2"; C_SYS = "#DDF0DD"
C_DEC = "#FFF3CC"; C_DEC_LINE = "#9A7D00"; C_STATE = "#CFE2F3"; C_FAIL = "#F7D9D6"
EDGE = "#444444"

class D:
    def __init__(self, w, h, bg="white"):
        self.w, self.h = w, h
        self.img = Image.new("RGB", (w*S, h*S), bg)
        self.dr = ImageDraw.Draw(self.img)
        self._f = {}
    def font(self, size, bold=False):
        k = (size, bold)
        if k not in self._f:
            self._f[k] = ImageFont.truetype(FBLD if bold else FREG, size*S)
        return self._f[k]
    def textblock(self, cx, cy, lines, size=18, bold=False, fill="black", lh=None):
        if isinstance(lines, str): lines = lines.split("\n")
        lh = lh or int(size*1.32)
        y0 = cy - lh*len(lines)/2 + lh/2
        f = self.font(size, bold)
        for i, ln in enumerate(lines):
            self.dr.text((cx*S, (y0+i*lh)*S), ln, font=f, fill=fill, anchor="mm")
    def ltext(self, x, y, s, size=18, bold=False, fill="black"):
        self.dr.text((x*S, y*S), s, font=self.font(size, bold), fill=fill, anchor="lm")
    def rect(self, x, y, w, h, fill="white", outline=EDGE, width=2, radius=0):
        b = [x*S, y*S, (x+w)*S, (y+h)*S]
        if radius > 0:
            self.dr.rounded_rectangle(b, radius=radius*S, fill=fill, outline=outline, width=width)
        else:
            self.dr.rectangle(b, fill=fill, outline=outline, width=width)
    def box(self, x, y, w, h, text, size=18, fill="white", outline=EDGE, radius=0, bold=False, tcolor="black", width=2):
        self.rect(x, y, w, h, fill, outline, width, radius)
        self.textblock(x+w/2, y+h/2, text, size, bold, tcolor)
    def ellipse(self, cx, cy, w, h, text, size=16, fill="white", outline=EDGE, width=2, bold=False):
        self.dr.ellipse([(cx-w/2)*S, (cy-h/2)*S, (cx+w/2)*S, (cy+h/2)*S], fill=fill, outline=outline, width=width)
        self.textblock(cx, cy, text, size, bold)
    def diamond(self, cx, cy, w, h, text, size=15, fill=C_DEC, outline=C_DEC_LINE, width=2):
        pts = [(cx*S, (cy-h/2)*S), ((cx+w/2)*S, cy*S), (cx*S, (cy+h/2)*S), ((cx-w/2)*S, cy*S)]
        self.dr.polygon(pts, fill=fill, outline=outline, width=width)
        self.textblock(cx, cy, text, size)
    def _head(self, x1, y1, x2, y2, color, size=11):
        ang = math.atan2(y2-y1, x2-x1); a = math.radians(25)
        p1 = (x2*S - size*S*math.cos(ang-a), y2*S - size*S*math.sin(ang-a))
        p2 = (x2*S - size*S*math.cos(ang+a), y2*S - size*S*math.sin(ang+a))
        self.dr.polygon([(x2*S, y2*S), p1, p2], fill=color)
    def _dash(self, x1, y1, x2, y2, color, width, dash=9, gap=6):
        d = math.hypot(x2-x1, y2-y1)
        if d == 0: return
        ux, uy = (x2-x1)/d, (y2-y1)/d; pos = 0
        while pos < d:
            a, b = pos, min(pos+dash, d)
            self.dr.line([(x1+ux*a)*S, (y1+uy*a)*S, (x1+ux*b)*S, (y1+uy*b)*S], fill=color, width=width)
            pos += dash+gap
    def arrow(self, x1, y1, x2, y2, color=EDGE, width=2, label=None, lsize=14, dashed=False, head=True, lpos=0.5, ldx=0, ldy=-12):
        if dashed: self._dash(x1, y1, x2, y2, color, width)
        else: self.dr.line([x1*S, y1*S, x2*S, y2*S], fill=color, width=width)
        if head: self._head(x1, y1, x2, y2, color)
        if label:
            mx, my = x1+(x2-x1)*lpos+ldx, y1+(y2-y1)*lpos+ldy
            self.label(mx, my, label, lsize, color)
    def label(self, cx, cy, text, size=14, color=EDGE):
        lines = text.split("\n"); f = self.font(size)
        lh = int(size*1.28); w = 0
        for ln in lines:
            bb = self.dr.textbbox((0, 0), ln, font=f); w = max(w, bb[2]-bb[0])
        h = lh*len(lines)*S
        self.dr.rectangle([cx*S-w/2-5, cy*S-h/2-3, cx*S+w/2+5, cy*S+h/2+3], fill="white")
        y0 = cy*S - h/2 + lh*S/2
        for i, ln in enumerate(lines):
            self.dr.text((cx*S, y0+i*lh*S), ln, font=f, fill=color, anchor="mm")
    def actor(self, cx, cy, name, size=17):
        s = S; col = "#333"; w = 3; r = 13
        self.dr.ellipse([(cx-r)*s, (cy-2*r)*s, (cx+r)*s, cy*s], outline=col, width=w)
        self.dr.line([cx*s, cy*s, cx*s, (cy+30)*s], fill=col, width=w)
        self.dr.line([(cx-20)*s, (cy+12)*s, (cx+20)*s, (cy+12)*s], fill=col, width=w)
        self.dr.line([cx*s, (cy+30)*s, (cx-16)*s, (cy+52)*s], fill=col, width=w)
        self.dr.line([cx*s, (cy+30)*s, (cx+16)*s, (cy+52)*s], fill=col, width=w)
        self.textblock(cx, cy+72, name, size, True)
    def start(self, cx, cy, r=13):
        self.dr.ellipse([(cx-r)*S, (cy-r)*S, (cx+r)*S, (cy+r)*S], fill="#333")
    def end(self, cx, cy, r=15):
        self.dr.ellipse([(cx-r)*S, (cy-r)*S, (cx+r)*S, (cy+r)*S], outline="#333", width=3)
        self.dr.ellipse([(cx-r*0.45)*S, (cy-r*0.45)*S, (cx+r*0.45)*S, (cy+r*0.45)*S], fill="#333")
    def umlclass(self, x, y, w, name, attrs, methods=None, title_fill=C_STATE, size=15, name_size=17):
        f = self.font(size); lh = int(size*1.55)
        nh = int(name_size*2.0)
        ah = lh*len(attrs)+12
        mh = (lh*len(methods)+12) if methods else 0
        h = nh+ah+mh
        self.rect(x, y, w, nh, fill=title_fill)
        self.textblock(x+w/2, y+nh/2, name, name_size, True)
        self.rect(x, y+nh, w, ah, fill="white")
        ay = y+nh+10+lh/2
        for i, a in enumerate(attrs):
            self.dr.text(((x+11)*S, (ay+i*lh)*S), a, font=f, fill="black", anchor="lm")
        if methods:
            self.rect(x, y+nh+ah, w, mh, fill="white")
            my = y+nh+ah+10+lh/2
            for i, m in enumerate(methods):
                self.dr.text(((x+11)*S, (my+i*lh)*S), m, font=f, fill="#333", anchor="lm")
        return (x, y, w, h)
    def cdiamond(self, cx, cy, size=11, fill="#333"):
        self.dr.polygon([(cx*S,(cy-size)*S),((cx+size)*S,cy*S),(cx*S,(cy+size)*S),((cx-size)*S,cy*S)], fill=fill, outline="#333")
    def save(self, p):
        self.img.save(p)


# ============================================================
# DIAGRAM 1 — USE CASE
# ============================================================
def use_case():
    d = D(1520, 1180)
    d.rect(370, 70, 770, 1060, fill="#FBFBFD", outline="#888", width=2)
    d.textblock(755, 102, "Library Management System", 21, True, "#333")
    # actors
    d.actor(110, 360, "Reader\n(Stu/Lec/Res)")
    d.actor(1410, 300, "Librarian")
    d.actor(1410, 870, "Administrator")
    d.actor(110, 960, "Scheduler\n(system job)")
    EW, EH = 312, 70
    # reader use cases
    rx = 560
    R = [("Search & view\ndocument details", 165),
         ("Manage cart &\ncreate loan request", 285),
         ("Confirm pickup\n(enter code)", 405),
         ("Renew / self-cancel\npending order", 525),
         ("View & mark\nnotifications", 645),
         ("Log in /\nChange password", 765)]
    for t, y in R:
        d.ellipse(rx, y, EW, EH, t, fill=C_READER)
        d.arrow(155, 362, rx-EW/2+8, y, head=False, width=2)
    # librarian use cases
    lx = 945
    L = [("Prepare & hand over\nbooks by code", 165),
         ("Process returns &\ncollect fines", 285),
         ("Cancel order /\nIn-house lending", 405),
         ("Manage books\n(CRUD + stock)", 525),
         ("Configure rules &\ndoc-type visibility", 645)]
    for t, y in L:
        d.ellipse(lx, y, EW, EH, t, fill=C_LIB)
        d.arrow(1365, 302, lx+EW/2-8, y, head=False, width=2)
    # admin use cases
    A = [("Manage accounts &\nlock/unlock cards", 815),
         ("Configure\npermission matrix", 935)]
    for t, y in A:
        d.ellipse(lx, y, EW, EH, t, fill=C_ADMIN)
        d.arrow(1365, 872, lx+EW/2-8, y, head=False, width=2)
    # scheduler use cases
    Suc = [("Auto-cancel expired\npickups (24h)", 905),
           ("Due / overdue\nreminders", 1025)]
    for t, y in Suc:
        d.ellipse(rx, y, EW, EH, t, fill=C_SYS)
        d.arrow(155, 962, rx-EW/2+8, y, head=False, width=2)
    d.save("/tmp/uml_usecase_en.png")


# ============================================================
# DIAGRAM 2 — ACTIVITY: Self-service borrowing
# ============================================================
def activity():
    d = D(1180, 1840)
    cx = 430          # main column
    fx = 880          # failure / branch column
    BW, BH = 360, 86
    DW, DH = 360, 150
    def act(y, t, fill=C_READER): d.box(cx-BW/2, y-BH/2, BW, BH, t, 16, fill=fill, radius=14)
    def dec(y, t): d.diamond(cx, y, DW, DH, t, 14)
    y = 60
    d.start(cx, y);
    d.arrow(cx, y+13, cx, 110)
    act(150, "Log in (Spring Security, BCrypt)")
    d.arrow(cx, 193, cx, 250)
    act(290, "Search documents\n(only visible doc types)")
    d.arrow(cx, 333, cx, 390)
    act(430, "Add books to cart")
    d.arrow(cx, 473, cx, 535)
    dec(610, "Pass 6 cart checks?\n(permission · duplicate · ≤5 ·\nnot restricted · visible ·\nin stock)")
    # fail -> back to add
    d.box(fx-150, 610-43, 300, 86, "Show error, keep cart", 15, fill=C_FAIL, radius=14)
    d.arrow(cx+DW/2, 610, fx-150, 610, label="No")
    d.arrow(fx, 610-43, fx, 430, head=True)
    d.arrow(fx, 430, cx+BW/2, 430, head=True)
    d.arrow(cx, 610+DH/2, cx, 745, label="Yes", ldx=22)
    act(785, "Submit loan request")
    d.arrow(cx, 828, cx, 885)
    dec(965, "RuleEngine 3-tier validate?\n(card locked · has overdue ·\nover per-type quota)")
    d.box(fx-150, 965-43, 300, 86, "Show error\n(Vietnamese message)", 15, fill=C_FAIL, radius=14)
    d.arrow(cx+DW/2, 965, fx-150, 965, label="No")
    d.arrow(fx, 965+43, fx, 1120)
    d.arrow(fx, 1120, cx+BW/2, 1120, head=True)
    d.arrow(cx, 965+DH/2, cx, 1085, label="Yes", ldx=22)
    d.box(cx-BW/2, 1085-50, BW, 100, "Create AWAITING_PICKUP loan\n+ 6-char code + hold copies (copies−1)\n+ send notification", 14, fill=C_SYS, radius=14)
    d.arrow(cx, 1135, cx, 1195)
    dec(1280, "Reader collects & enters\ncorrect code within 24h?")
    # timeout branch
    d.box(fx-160, 1280-58, 320, 116, "Scheduler auto-cancels order\n(not collected within 24h)\nrestores copies → CANCELLED", 14, fill=C_FAIL, radius=14)
    d.arrow(cx+DW/2, 1280, fx-160, 1280, label="No / expired")
    d.end(fx, 1480); d.arrow(fx, 1280+58, fx, 1480-15)
    d.arrow(cx, 1280+DH/2, cx, 1410, label="Yes", ldx=22)
    d.box(cx-BW/2, 1410-50, BW, 100, "confirmPickup → BORROWED\nDue date set by role × doc type\n(may renew if allowed)", 14, fill=C_LIB, radius=14)
    d.arrow(cx, 1460, cx, 1520)
    d.box(cx-BW/2, 1520-43, BW, 86, "Librarian processes return → fine\n(overdue days × 2,000 VND) → collect", 15, fill=C_LIB, radius=14)
    d.arrow(cx, 1563, cx, 1615)
    dec(1690, "All items returned\n& all fines paid?")
    # loop back if not
    d.arrow(cx-DW/2, 1690, 150, 1690, label="Not yet")
    d.arrow(150, 1690, 150, 1520, head=False); d.arrow(150, 1520, cx-BW/2, 1520, head=True)
    d.arrow(cx, 1690+DH/2, cx, 1775, label="Yes", ldx=22)
    d.box(cx-110, 1775-26, 220, 56, "COMPLETED", 16, fill=C_SYS, radius=14, bold=True)
    d.end(cx, 1838-18) if False else None
    # final node
    d.dr.line([cx*S,(1801)*S,cx*S,(1812)*S], fill=EDGE, width=2*S)
    d.end(cx, 1822)
    d.save("/tmp/uml_activity_en.png")


# ============================================================
# DIAGRAM 3 — STATE MACHINE: Loan lifecycle
# ============================================================
def state_machine():
    d = D(1500, 1060)
    SW, SH = 300, 92
    def st(x, y, t, fill=C_STATE): d.box(x-SW/2, y-SH/2, SW, SH, t, 17, fill=fill, radius=18, bold=True)
    d.start(120, 180)
    st(360, 180, "AWAITING_PICKUP")
    st(820, 180, "BORROWED")
    st(1280, 180, "COMPLETED", fill=C_SYS)
    d.arrow(133, 180, 360-SW/2, 180, label="submitLoan /\ncreate code + hold copies", ldy=-28)
    d.arrow(360+SW/2, 180, 820-SW/2, 180, label="confirmPickup [code OK] /\nset due date", ldy=-28)
    d.arrow(820+SW/2, 180, 1280-SW/2, 180, label="all returned &\nfines paid /\nrefreshCompletion", ldy=-36)
    # self loops on BORROWED
    d.arrow(820-70, 180-SH/2, 820-70, 92, head=False); d.arrow(820-70, 92, 820+70, 92, head=False); d.arrow(820+70, 92, 820+70, 180-SH/2, head=True)
    d.label(820, 74, "renewBook [renewals left] / +renewalDays", 13)
    d.arrow(820-70, 180+SH/2, 820-70, 300, head=False); d.arrow(820-70, 300, 820+70, 300, head=False); d.arrow(820+70, 300, 820+70, 180+SH/2, head=True)
    d.label(820, 318, "returnBook / compute fine + restore copies", 13)
    # COMPLETED final
    d.end(1280, 360); d.arrow(1280, 180+SH/2, 1280, 360-15)
    # CANCELLED under AWAITING_PICKUP
    st(360, 560, "CANCELLED", fill=C_FAIL)
    d.arrow(360, 180+SH/2, 360, 560-SH/2)
    d.label(610, 370, "cancel (reader / librarian)\nor 24h timeout (scheduler) /\nrestore copies", 13)
    d.end(360, 740); d.arrow(360, 560+SH/2, 360, 740-15)
    # LoanDetail lifecycle strip (bottom)
    d.textblock(235, 900, "LoanDetail\nlifecycle:", 16, True, "#333")
    for t, x in [("RESERVED", 640), ("BORROWING", 920), ("RETURNED", 1200)]:
        d.box(x-120, 900-30, 240, 60, t, 15, fill="#EEEEEE", radius=14)
    d.arrow(640+120, 900, 920-120, 900, label="pickup", ldy=-20, lsize=12)
    d.arrow(920+120, 900, 1200-120, 900, label="return", ldy=-20, lsize=12)
    d.label(920, 968, "(OVERDUE = computed when dueDate < today; not stored as a separate state)", 12, "#666")
    d.save("/tmp/uml_state_en.png")


# ============================================================
# DIAGRAM 4 — CLASS DIAGRAM (domain model)
# ============================================================
def class_diagram():
    d = D(1520, 1120)
    user = d.umlclass(70, 90, 330, "User",
        ["- id: Long", "- studentCode: String «unique»", "- fullName: String", "- passwordHash: String «BCrypt»",
         "- role: UserRole", "- cardStatus: CardStatus", "- createdAt: DateTime"])
    loan = d.umlclass(590, 70, 360, "Loan",
        ["- id: Long", "- createdAt: DateTime", "- pickupCode: String(6)", "- pickupDeadline: DateTime",
         "- pickedUpAt: DateTime", "- status: LoanStatus", "- inHouse: boolean", "- cancelReason: String",
         "- cancelledByRole: UserRole"])
    det = d.umlclass(590, 560, 360, "LoanDetail",
        ["- id: Long", "- dueDate: Date", "- returnDate: DateTime", "- renewalCount: int",
         "- conditionStatus: BookCondition", "- fineAmount: BigDecimal", "- finePaid: boolean",
         "- status: LoanDetailStatus"])
    book = d.umlclass(1140, 560, 330, "Book",
        ["- id: Long", "- title / author: String", "- isbn: String", "- category: String",
         "- docType: DocType", "- totalCopies: int", "- availableCopies: int", "- classificationCode: String"])
    rule = d.umlclass(1140, 90, 330, "BorrowingRule",
        ["- id: Long", "- userRole: UserRole", "- docType: DocType", "- maxQuantity: int",
         "- borrowDays: Integer «null=∞»", "- maxRenewals: int", "- renewalDays: Integer", "- visible: boolean"])
    notif = d.umlclass(70, 470, 330, "Notification",
        ["- id: Long", "- type: NotificationType", "- message: String", "- link: String",
         "- read: boolean", "- createdAt: DateTime"])
    rp = d.umlclass(70, 800, 330, "RolePermission",
        ["- id: Long", "- userRole: UserRole", "- permission: Permission", "- granted: boolean"])
    # User 1 - 0..* Loan
    d.arrow(user[0]+user[2], 200, loan[0], 200, head=False)
    d.label(468, 188, "1", 14); d.label(560, 188, "0..*", 14)
    # Loan 1 -- 1..* LoanDetail (composition, filled diamond at the whole/Loan side)
    d.arrow(770, loan[1]+loan[3], 770, det[1], head=False)
    d.cdiamond(770, loan[1]+loan[3]+1)
    d.label(742, loan[1]+loan[3]+34, "1", 14); d.label(748, det[1]-22, "1..*", 14)
    # LoanDetail * -- 1 Book
    d.arrow(det[0]+det[2], 660, book[0], 660, head=False)
    d.label(998, 648, "*", 14); d.label(1098, 648, "1", 14)
    # User 1 - 0..* Notification
    d.arrow(user[0]+70, user[1]+user[3], user[0]+70, notif[1], head=False)
    d.label(user[0]+96, user[1]+user[3]+20, "1", 14); d.label(user[0]+102, notif[1]-20, "0..*", 14)
    # Config note (BorrowingRule & RolePermission are enum-keyed lookup tables)
    d.rect(540, 770, 470, 110, fill="#FFFDF0", outline="#C9A227", width=2)
    d.textblock(775, 825, "BorrowingRule is keyed on (userRole, docType);\nRolePermission is keyed on (userRole, permission).\nTwo enum-keyed config lookup tables, no FK to User.", 14, fill="#7a5c00")
    d.save("/tmp/uml_class_en.png")


use_case(); activity(); state_machine(); class_diagram()
print("done EN: usecase, activity, state, class")
for f in ["usecase","activity","state","class"]:
    im = Image.open(f"/tmp/uml_{f}_en.png"); print(f, im.size)
