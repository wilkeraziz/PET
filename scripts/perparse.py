import re
import os
import sys
from hter import HTER, NULLHTER
from codecs import open
from nltk.tokenize import word_tokenize, wordpunct_tokenize
import argparse
import collections
from xml.dom.minidom import parse as xml_parse

def tomilli(b, v):
    if b == 'd':
        return v * 24 * 60 * 60 * 1000
    if b == 'h':
        return v * 60 * 60 * 1000
    if b == 'm':
        return v * 60 * 1000
    if b == 's':
        return v * 1000
    if b == 'M':
        return v

def parse_dur(s):
    s = re.sub(',([0-9]+)', '\\1M', s)
    values = [int(x) if x else 0 for x in re.split('[dhmsM]', s)][:-1]
    bases = re.split('\d+', s)[1:]
    return sum(tomilli(b, v) for v, b in zip(values, bases))
    
def parse_text(element):
    rc = []
    for node in element.childNodes:
        if node.nodeType == node.TEXT_NODE:
            rc.append(node.data.encode('utf-8'))
    return ''.join(rc) #.encode('utf-8')

def parse_attr(element, attr):
    return element.getAttribute(attr).encode('utf-8')

def parse_time(strtime):
    first, second = strtime.split(',')
    time = int(second) # mili part


def cmd_parse():
    parser = argparse.ArgumentParser(description="Parse PET's PER files")
    parser.add_argument("who", type=str, help="annotator")
    parser.add_argument("input", type=str, help="per file")
    parser.add_argument("--hter", type=str, help="path to HTER jar")
    parser.add_argument("--tmp", type=str, default='tmp', help="a temp dir to compute HTER")
    parser.add_argument("--assessments", type=str, help="a comma separated list of assessment ids")
    args = parser.parse_args()
    if not os.path.exists(args.tmp):
        os.makedirs(args.tmp)
    return args


class Assessment(object):

    def __init__(self, criterion, value):
        self._criterion = criterion
        self._value = value

    @property
    def criterion(self):
        return self._criterion

    @property
    def value(self):
        return self._value

    def __str__(self):
        return '%s: %s' % (self.criterion, self.value)

    @classmethod
    def parse(cls, xml):
        criterion = parse_attr(xml, 'id')
        # compatibility with older versions
        criterion = '_'.join(criterion.split()).lower()
        # --------------------------------

        value = parse_text(xml.getElementsByTagName('score')[0])
        try:
            value = int(value.split('.')[0])
        except:
            value = str(value.split('.')[0])
        return Assessment(criterion, value)

class Indicator(object):

    def __init__(self, iid, itype, value):
        self._id = iid
        self._type = itype
        self._value = value

    @property
    def id(self):
        return self._id

    @property
    def type(self):
        return self._type

    @property
    def value(self):
        return self._value

    def __str__(self):
        return '[%s] %s: %s' % (self.type, self.id, self.value)

    @classmethod
    def parse(cls, xml):
        iid = parse_attr(xml, 'id')
        itype = parse_attr(xml, 'type')
        value = parse_text(xml)
        if itype == 'count':
            value = int(value)
        elif itype == 'time':
            value = parse_dur(value)
        elif itype == 'flag':
            value = value in ('True', 'true', 'Yes', 'yes', '1')
        return Indicator(iid, itype, value)


class Segment(object):

    S, MT, PE, HT = 'S', 'MT', 'PE', 'HT'

    def __init__(self, text, producer, segtype):
        self._text = text
        self._producer = producer.split('.')[-1]
        self._type = segtype
        self._tokens = word_tokenize(text)
    
    def __len__(self):
        return len(self._tokens)

    @property
    def tokens(self):
        return self._tokens

    @property
    def text(self):
        return self._text

    @property
    def producer(self):
        return self._producer

    @property
    def type(self):
        return self._type

    def __str__(self):
        print self.producer
        return '[%s] %s' % (self.producer, self.text)

    @classmethod
    def parse(cls, xml):
        text = parse_text(xml)
        producer = parse_attr(xml, 'producer')
        return Segment(text, producer, xml.tagName)

class Unit(object):
    
    __COMPATIBILITY_ID = 0

    EmptyMT = Segment('','<none>','MT')
    EmptyS = Segment('','<none>','S')
    EmptyPE = Segment('','<none>','PE')
    EmptyHT = Segment('','<none>','HT')

    def __init__(self, uid, sources, mts, pes, assessments, indicators, hter = NULLHTER()):
        self._id = uid
        self._sources = sources
        self._mts = mts
        self._pes = pes
        self._assessments = assessments
        self._indicators = indicators
        self._slen = 0 if len(sources) == 0 else sum(len(s) for s in sources) / len(sources)
        self._mlen = 0 if len(mts) == 0 else sum(len(m) for m in mts) / len(mts)
        self._plen = 0 if len(pes) == 0 else len(pes[-1]) 
        self._hter = hter

    def __str__(self):
        return '%s\n S: %s\n S: %s\n M: %s\n P: %s\n editing time: %fs\n seconds per word: %f' % (self.id,
                ' | '.join(str(s) for s in self._sources), 
                ' '.join(self._sources[0].tokens),
                ' | '.join(str(m) for m in self._mts),
                self.pe,
                self.editing/1000,
                self.editingPerSToken/1000)

    def source0(self):
        return self._sources[0] if self._sources else Unit.EmptyS

    def mt0(self):
        return self._mts[0] if self._mts else Unit.EmptyMT

    @property
    def hter(self):
        return self._hter

    @property
    def id(self):
        return self._id

    @property
    def pe(self):
        return self._pes[-1] if self._pes else Unit.EmptyPE

    @property
    def editingPerSToken(self):
        return float(self._editing)/self.slen

    @property
    def slen(self):
        return self._slen

    @property
    def mlen(self):
        return self._mlen

    @property
    def plen(self):
        return self._plen

    def assessment(self, criterion):
        return self._assessments[criterion][-1].value

    def reduce(self, indicator, op=sum):
        return op(i.value for i in self._indicators[indicator])

    @property
    def editing(self):
        return self.reduce('editing')

    @property
    def assessing(self):
        return self.reduce('assessing')
    
    @property
    def letters(self):
        return self.reduce('letter-keys')

    @property
    def digits(self):
        return self.reduce('digit-keys')

    @property
    def spaces(self):
        return self.reduce('white-keys')

    @property
    def symbols(self):
        return self.reduce('symbol-keys')

    @property
    def navigation(self):
        return self.reduce('navigation-keys')

    @property
    def erase(self):
        return self.reduce('erase-keys')

    @property
    def commands(self):
        return self.reduce('copy-keys') + self.reduce('cut-keys') + self.reduce('paste-keys') + self.reduce('do-keys')

    @property
    def visiblekeys(self):
        return self.letters + self.digits + self.spaces + self.symbols

    @property
    def keystrokes(self):
        """Does not consider commands and navigation"""
        return self.letters + self.digits + self.spaces + self.symbols + self.erase
    
    @property
    def allkeys(self):
        return self.letters + self.digits + self.spaces + self.symbols + self.erase + self.commands + self.navigation

    @property
    def insertions(self):
        return sum(1 for i in self._indicators['insertion'])
    
    @property
    def deletions(self):
        return sum(1 for i in self._indicators['deletion'])
    
    @property
    def substitutions(self):
        return sum(1 for i in self._indicators['substitution'])

    @property
    def shifts(self):
        return sum(1 for i in self._indicators['shift'])

    @classmethod
    def parse(cls, xml, hter = None):

        try:
            uid = int(parse_attr(xml, 'id'))
        except:
            Unit.__COMPATIBILITY_ID += 1
            uid = Unit.__COMPATIBILITY_ID
        status = parse_attr(xml, 'status')
        sources = []
        mts = []
        pes = []
        assessments = collections.defaultdict(list)
        indicators = collections.defaultdict(list)
        for src in xml.getElementsByTagName('S'):
            s = Segment.parse(src)
            sources.append(s)
        for mt in xml.getElementsByTagName('MT'):
            m = Segment.parse(mt)
            mts.append(m)
        for ht in xml.getElementsByTagName('HT'):
            h = Segment.parse(ht)
            pes.append(h)
        for pe in xml.getElementsByTagName('PE'):
            p = Segment.parse(pe)
            pes.append(p)
        for assessment in xml.getElementsByTagName('assessment'):
            a = Assessment.parse(assessment)
            assessments[a.criterion].append(a)
        for indicator in xml.getElementsByTagName('indicator'):
            i = Indicator.parse(indicator)
            indicators[i.id].append(i)

        #assert len(pes) > 0, 'Unit %d has no PEs' % uid
        if len(mts) > 0 and len(pes) > 0 and hter is not None:
            return Unit(uid, sources, mts, pes, assessments, indicators, hter.hter(mts[-1].text, pes[-1].text))
        else:
            return Unit(uid, sources, mts, pes, assessments, indicators)


class PER(object):

    def __init__(self, path, who, hter):
        self._xml = xml_parse(path)
        self._who = who
        self._units = []

        
        xml_units = self._xml.getElementsByTagName('unit')

        # compatibility with older versions
        if not xml_units:
            xml_units = self._xml.getElementsByTagName('task')

        for unit in xml_units:
            self._units.append(Unit.parse(unit, hter))

    def __iter__(self):
        for u in self._units:
            yield u

    @property
    def who(self):
        return self._who

def main(args):

    hter = HTER(args.hter, args.tmp) if args.hter else None

    assessments = args.assessments.split(',') if args.assessments else []
    columns = 'who\ttype\tsrc\tsys\ttime\tslen\tmlen\tplen\tletters\tdigits\tspaces\tsymbols\tnavigation\terase\tcommands\tvisible\tkeystrokes\tallkeys\tinsertions\tdeletions\tsubstitutions\tshifts'.split('\t')
    if hter:
        columns.extend('hter\thter_ins\thter_del\thter_sub\thter_shift\thter_wdsh\thter_errors\thter_words'.split('\t'))
    if assessments:
        columns.append('assessing')
        columns.extend(assessments)
    columns.extend('S\tMT\tPE'.split())

    print '\t'.join(columns)

    for path in args.input.split(','):
        print >> sys.stderr, 'Parsing', path
        per = PER(path, args.who, hter)
        for u in per:
            row = [per.who, u.pe.type, u.id, u.pe.producer, u.editing, u.slen, u.mlen, u.plen, 
                    u.letters, u.digits, u.spaces, u.symbols, u.navigation, u.erase,
                    u.commands, u.visiblekeys, u.keystrokes, u.allkeys, 
                    u.insertions, u.deletions, u.substitutions, u.shifts]
            if hter:
                row.extend([u.hter, u.hter.I, u.hter.D, u.hter.S, u.hter.Sh, u.hter.WSh, u.hter.errors, u.hter.words])
            if assessments:
                row.append(u.assessing)
                row.extend(u.assessment(aid) for aid in assessments)
            row.extend([u.source0().text, u.mt0().text, u.pe.text])
            print '\t'.join(str(x) for x in row)


if __name__ == '__main__':
    args = cmd_parse()
    main(args)
