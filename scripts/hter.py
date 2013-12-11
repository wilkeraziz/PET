import subprocess as sp
import os.path

class HTERScore(float):

    def __new__(cls, score, I, D, S, Sh, WSh, errors, words):
        new = super(HTERScore, cls).__new__(cls, score)
        new.I = I
        new.D = D
        new.S = S
        new.Sh = Sh
        new.WSh = WSh
        new.errors = errors
        new.words = words
        return new

class NULLHTER(float):

    def __new__(cls):
        new = super(NULLHTER, cls).__new__(cls, 0)
        new.I = 0
        new.D = 0
        new.S = 0
        new.Sh = 0
        new.WSh = 0
        new.errors = 0
        new.words = 0
        return new


class HTER(object):

    def __init__(self, jar, tmp):
        self._jar = jar
        self._tmp = tmp

    def format_snt(self, sid, snt):
        return '%s (%s)' % (snt, sid)

    def write_snts(self, snts, path):
        with open(path, 'w') as f:
            for sid, snt in enumerate(snts):
                print >> f, self.format_snt(sid + 1, snt)

    def hter(self, mt, pe):
        self.write_snts([mt], os.path.join(self._tmp, 'mt'))
        self.write_snts([pe], os.path.join(self._tmp, 'pe'))
        args = ['java', '-jar', self._jar, 
                '-r', os.path.join(self._tmp, 'pe'), 
                '-h', os.path.join(self._tmp, 'mt'), 
                '-N', '-o', 'sum', 
                '-n', os.path.join(self._tmp, 'hter')]
        proc = sp.Popen(args, stdout = sp.PIPE)
        output = proc.wait()

        with open('%s/hter.sum' % self._tmp) as f:
            Ins, Del, Sub, Shft, WdSh, NumEr, NumWd, Ter = 0, 0, 0, 0, 0, 0, 0, 0
            for line in f:
                if line.startswith('1:1'):
                    sid, Ins, Del, Sub, Shft, WdSh, NumEr, NumWd, Ter = line.strip().replace('|', '').split()
                    Ins, Del, Sub, Shft, WdSh, NumEr, NumWd = [int(float(x)) for x in (Ins, Del, Sub, Shft, WdSh, NumEr, NumWd)]
                    Ter = float(Ter)/100
                    break
        return HTERScore(Ter, Ins, Del, Sub, Shft, WdSh, NumEr, NumWd)
        
