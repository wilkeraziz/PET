import subprocess as sp

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
        self.write_snts([mt], '%s/mt' % self._tmp)
        self.write_snts([pe], '%s/pe' % self._tmp)
        cmd = "%s -r %s/pe -h %s/mt -N -o sum -n %s/hter | egrep 'TER'" % (self._jar, self._tmp, self._tmp, self._tmp)
        proc = sp.Popen(cmd, shell = True, stdout = sp.PIPE, executable = '/bin/bash')
        output = proc.wait()
        #score, badchunks, chunks = 0, 0, 0
        #for line in proc.stdout:
        #    score, badchunks, chunks = line.strip().replace('(','').replace(')','').replace('/', ' ').replace('Total TER: ', '').split()
        #score, badchunks, chunks = float(score), float(badchunks), float(chunks)

        with open('%s/hter.sum' % self._tmp) as f:
            Ins, Del, Sub, Shft, WdSh, NumEr, NumWd, Ter = 0, 0, 0, 0, 0, 0, 0, 0
            for line in f:
                if line.startswith('1:1'):
                    sid, Ins, Del, Sub, Shft, WdSh, NumEr, NumWd, Ter = line.strip().replace('|', '').split()
                    Ins, Del, Sub, Shft, WdSh, NumEr, NumWd = [int(float(x)) for x in (Ins, Del, Sub, Shft, WdSh, NumEr, NumWd)]
                    Ter = float(Ter)/100
                    break
        return HTERScore(Ter, Ins, Del, Sub, Shft, WdSh, NumEr, NumWd)
        
